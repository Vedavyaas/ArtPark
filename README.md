# ArtPark: AI-Adaptive Onboarding Engine 🚀

ArtPark is a next-generation corporate onboarding and up-skilling engine designed to eliminate redundant training by dynamically analyzing candidate skill-gaps and rendering chronologically optimized learning roadmaps. 

This repository was purpose-built to execute on the challenge of **"Dynamic Pathing vs One-Size-Fits-All"** curricula.

## 🧠 System Architecture: Neuro-Symbolic AI
Unlike naive applications that rely entirely on unpredictable, hallucination-prone Large Language Models to generate business logic, ArtPark utilizes a robust **Neuro-Symbolic** (Hybrid Core) architecture to guarantee 100% deterministic, auditable, and mathematically flawless training paths.

### 1. Data Extraction Pipeline (Gemini 2.5)
We utilize Google Gemini strictly as a highly constrained NLP Parsing Engine (`response_mime_type="application/json"`). It scans raw Job Descriptions and Resumes to extract pure, algebraic Arrays of keywords representing the candidate's exact skill topology. 

### 2. The Algorithmic Adaptive Logic Engine (Python)
The hackathon specifically requires original Adaptive Logic. We engineered a proprietary Python backend utilizing **Set-Theory** and a custom **Length-Agnostic Overlap Coefficient Matrix**.
*   The engine mathematically subtracts the candidate's skills from the JD's requirements.
*   It then runs a **Jaccard-style token overlap** to score every module in our native `course_catalog.json` against the missing skill gaps, mapping the best courses dynamically.

### 3. Directed Acyclic Graph (DAG) Prerequisite Traversal
To achieve true curriculum depth, the Engine utilizes a custom **Topological DFS (Depth-First Search) Algorithm**. 
*   If a candidate requires an advanced skill (e.g. *Kubernetes*), the DAG Traversal automatically injects mathematical prerequisites (e.g. *Linux Fundamentals*) chronologically before the advanced course.
*   **Dynamic Pruning:** If the mathematics determine the candidate *already masters* the prerequisite, the DAG intelligently drops the branch, ensuring zero redundant training time!

## 📊 ROI Metrics Dashboard
Real-world deployments require measurable impact. The frontend features a beautiful, glassmorphism Metrics Overlay that calculates the exact percentage of **Profile Match** and the volume of **Missing Skills**, visually demonstrating the precise ROI of the generated training curriculum before the roadmap is even explored.

## 🛠️ Tech Stack
*   **Frontend:** Vanilla JS, HTML5, CSS3 Glassmorphism UI
*   **Middleware (Parsing):** Java Spring Boot & Apache PDFBox (File IO & Structural Extraction)
*   **Backend (Adaptive Engine):** Python 3, FastAPI, Google GenAI SDK

## 🚀 How to Run Locally

### 1. Start the Java Parser
```bash
./mvnw spring-boot:run
```

### 2. Start the Python Adaptive Engine
```bash
cd python-ml-backend
pip install -r requirements.txt
python main.py
```
*(Ensure you have a `.env` file containing `GEMINI_API_KEY=your_key` in the Python directory).*

---
*Built for the Hackathon Challenge. Adaptive Engine logic is 100% original implementation.*