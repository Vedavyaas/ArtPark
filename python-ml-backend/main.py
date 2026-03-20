from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from fastapi.responses import PlainTextResponse
from google import genai
from google.genai import types
import os
import json
import re
from dotenv import load_dotenv

load_dotenv()

app = FastAPI(title="ArtPark Original DAG Adaptive Engine")

try:
    client = genai.Client()
except Exception as e:
    print(f"Warning: Failed to initialize Gemini client: {e}")
    client = None

try:
    with open("course_catalog.json", "r") as f:
        COURSE_CATALOG = json.load(f)
        # Create a fast-lookup dictionary for graph traversal
        COURSE_DICT = {c['id']: c for c in COURSE_CATALOG}
except Exception as e:
    COURSE_CATALOG = []
    COURSE_DICT = {}
    print("Warning: course_catalog.json not found.")

class RoadmapRequest(BaseModel):
    skills: str
    experience: str
    requirements: str

# --- 1. NLP Tokenization Engine ---
def tokenize(text: str) -> set:
    text = str(text).lower()
    tokens = re.findall(r'[a-z0-9]+', text)
    stop_words = {'and', 'the', 'to', 'of', 'in', 'for', 'with', 'a', 'an', 'is', 'on', 'as', 'at', 'by', 'experience', 'skills', 'required'}
    return set([t for t in tokens if t not in stop_words])

def compute_similarity(tokens_a: set, tokens_b: set) -> float:
    if not tokens_a or not tokens_b: return 0.0
    # Overlap Coefficient: divides by the smaller set size to handle length disparities!
    return len(tokens_a.intersection(tokens_b)) / max(1, min(len(tokens_a), len(tokens_b)))

# --- 2. LLM Parser ---
def extract_skills_via_llm(resume_text: str, jd_text: str) -> dict:
    if not client: return {"resume_skills": [], "jd_skills": []}
    system_prompt = """You are a strictly constrained Data Extraction JSON API.
Extract all hard technical skills, tools, and domain abilities from the provided texts.
You MUST respond with ONLY raw, valid JSON containing exactly these two keys:
{"resume_skills": ["skill1"], "jd_skills": ["req1"]}"""
    prompt = f"Resume Extraction Source:\n{resume_text}\n\nJob Description Extraction Source:\n{jd_text}"
    try:
        res = client.models.generate_content(
            model='gemini-2.5-flash', contents=prompt,
            config=types.GenerateContentConfig(system_instruction=system_prompt, temperature=0.1, response_mime_type="application/json")
        )
        return json.loads(res.text.strip())
    except: return {"resume_skills": [], "jd_skills": []}

# --- 3. Topological Graph Traversal Engine (DAG) ---
def get_prerequisite_chain(course_id: str, visited: set, curriculum_order: list, reason_map: dict, trigger_reason: str, resume_tokens: set):
    """DFS Topological Sort that dynamically prunes branches of knowledge the candidate already masters."""
    if course_id in visited or course_id not in COURSE_DICT:
        return
    
    course_node = COURSE_DICT[course_id]
    course_tokens = tokenize(course_node['title']) # Evaluate stringently against just the title
    mastery_score = compute_similarity(course_tokens, resume_tokens)
    
    # Dynamic Pruning: If this is a deep prerequisite AND the resume already mathematically overlaps with it, SKIP IT!
    if "Topological Prerequisite" in trigger_reason and mastery_score >= 0.3:
        visited.add(course_id)
        return

    visited.add(course_id)
    
    # Traverse dependencies (Prerequisites must be completed first)
    for prereq_id in course_node.get("prerequisites", []):
        if prereq_id not in visited:
            # Propagate reasoning downward
            prereq_reason = f"Topological Prerequisite mathematically required to unlock '{course_node['title']}'."
            get_prerequisite_chain(prereq_id, visited, curriculum_order, reason_map, prereq_reason, resume_tokens)
            
    # Add self to path AFTER dependencies are resolved
    curriculum_order.append(course_id)
    if course_id not in reason_map:
        reason_map[course_id] = trigger_reason

@app.post("/generate-roadmap")
async def generate_roadmap(request: RoadmapRequest):
    if not client: raise HTTPException(status_code=500, detail="Gemini client not initialized.")

    parsed_data = extract_skills_via_llm(request.skills + " " + request.experience, request.requirements)
    resume_skills = set([s.lower() for s in parsed_data.get("resume_skills", [])])
    jd_skills = set([s.lower() for s in parsed_data.get("jd_skills", [])])

    missing_skills = jd_skills - resume_skills
    if not missing_skills: missing_skills = tokenize(request.requirements) - tokenize(request.skills)

    # Compute Met-Metrics for UI Overlay using raw Token Math (ignoring string formatting)
    jd_tokens = tokenize(" ".join(jd_skills) if jd_skills else request.requirements)
    total_resume_tokens = tokenize(" ".join(resume_skills) if resume_skills else request.skills + " " + request.experience)
    missing_tokens = jd_tokens - total_resume_tokens
    
    total_jd = max(1, len(jd_tokens))
    match_percentage = int(((total_jd - len(missing_tokens)) / total_jd) * 100)
    match_percentage = max(0, min(100, match_percentage))

    # Find Direct Matches using Heuristic
    direct_course_matches = []
    for course in COURSE_CATALOG:
        course_tokens = tokenize(course['title'] + " " + course['description'])
        best_score, matched_skill = 0, ""
        for skill in missing_skills:
            score = compute_similarity(tokenize(skill), course_tokens)
            if score > 0.3 and score > best_score:
                best_score, matched_skill = score, skill
        if best_score > 0:
            direct_course_matches.append((course['id'], best_score, matched_skill))

    direct_course_matches.sort(key=lambda x: x[1], reverse=True)
    top_targets = direct_course_matches[:3] # Pick top 3 direct gaps to map

    resume_tokens = tokenize(request.skills + " " + request.experience)

    # Run Topological Traversal
    visited_nodes = set()
    ordered_curriculum = []
    reasoning_dict = {}

    for c_id, score, skill in top_targets:
        target_reason = f"Main requirement. JD specifically demanded proficiency in [{skill.title()}]."
        get_prerequisite_chain(c_id, visited_nodes, ordered_curriculum, reasoning_dict, target_reason, resume_tokens)

    csv_lines = ["Title,Description,Estimated Duration,Reasoning"]
    for c_id in ordered_curriculum:
        c = COURSE_DICT[c_id]
        title, desc, duration = f'"{c["title"]}"', f'"{c["description"]}"', f'"{c["duration"]}"'
        reasoning = f'"{reasoning_dict[c_id]}"'
        csv_lines.append(f"{title},{desc},{duration},{reasoning}")

    if len(csv_lines) == 1:
        csv_lines.append('"Foundational Corporate Overview","Standard induction mapped out due to zero mathematical gap.","1 Week","Standard curriculum progression."')
        
    return {
        "metrics": {
            "match_percentage": match_percentage,
            "missing_skills_count": len(missing_skills),
            "courses_required": len(csv_lines) - 1
        },
        "csv": "\n".join(csv_lines)
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
