document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('fetch-roadmap-form');
    const nameInput = document.getElementById('roadmap-name');
    const fetchBtn = document.getElementById('fetch-btn');
    const messageBox = document.getElementById('roadmap-message');
    const timeline = document.getElementById('roadmap-timeline');

    function showMessage(msg, type) {
        messageBox.textContent = msg;
        messageBox.className = `feedback-message ${type} show`;
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const candidateName = nameInput.value.trim();
        if (!candidateName) return;

        fetchBtn.classList.add('loading');
        fetchBtn.disabled = true;
        messageBox.classList.remove('show');
        timeline.innerHTML = ''; // clear timeline

        try {
            const res = await fetch(`/get/roadmap?name=${encodeURIComponent(candidateName)}`);
            if (res.ok) {
                const csvText = await res.text();
                // Check if the response implies failure
                if (csvText === "Username not found" || csvText === "Not updated" || !csvText) {
                    showMessage(csvText || "Roadmap not available yet.", 'error');
                } else {
                    renderRoadmap(csvText);
                }
            } else {
                showMessage("Failed to fetch roadmap data.", "error");
            }
        } catch (error) {
            console.error(error);
            showMessage("Network error occurred.", "error");
        } finally {
            fetchBtn.classList.remove('loading');
            fetchBtn.disabled = false;
        }
    });

    function renderRoadmap(csvString) {
        // Parse simple CSV string
        const lines = csvString.trim().split('\n');
        if (lines.length <= 1) {
            showMessage("Invalid Roadmap data format.", "error");
            return;
        }
        
        // Remove headers
        const dataLines = lines.slice(1);

        dataLines.forEach((line, index) => {
            // Very simple CSV parser, assuming no commas within fields 
            // Phase,Title,Description,Duration
            // If fields do have text commas, a better CSV parser is needed
            // A simple regex to avoid breaking upon standard text:
            const match = line.split(',');
            if (match.length >= 3) {
                const phase = match[0];
                const title = match[1];
                let desc = match.slice(2, match.length - 1).join(',');
                let duration = match[match.length - 1];
                
                // Constructing node structure
                let node = document.createElement('div');
                node.className = 'timeline-item';
                node.style.animationDelay = `${index * 0.15}s`;
                
                node.innerHTML = `
                    <div class="timeline-marker">
                        <div class="marker-dot"></div>
                        <div class="marker-line"></div>
                    </div>
                    <div class="timeline-content glass-effect">
                        <div class="timeline-header">
                            <span class="timeline-phase">Phase ${phase}</span>
                            <span class="timeline-duration">${duration}</span>
                        </div>
                        <h3>${title}</h3>
                        <p>${desc}</p>
                    </div>
                `;
                timeline.appendChild(node);
            }
        });
    }
});
