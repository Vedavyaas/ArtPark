document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('ingest-form');
    const submitBtn = document.getElementById('submit-btn');
    const feedbackMsg = document.getElementById('feedback-message');
    const timeline = document.getElementById('roadmap-timeline');

    function setupDropZone(dropId, inputId, infoId, nameId, removeId) {
        const dropArea = document.getElementById(dropId);
        const fileInput = document.getElementById(inputId);
        const fileInfo = document.getElementById(infoId);
        const fileNameDisplay = document.getElementById(nameId);
        const removeFileBtn = document.getElementById(removeId);

        dropArea.addEventListener('click', () => fileInput.click());

        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            dropArea.addEventListener(eventName, e => {
                e.preventDefault(); e.stopPropagation();
            }, false);
        });

        ['dragenter', 'dragover'].forEach(eventName => {
            dropArea.addEventListener(eventName, () => dropArea.classList.add('dragover'), false);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            dropArea.addEventListener(eventName, () => dropArea.classList.remove('dragover'), false);
        });

        dropArea.addEventListener('drop', (e) => handleFiles(e.dataTransfer.files));
        fileInput.addEventListener('change', function() { handleFiles(this.files); });

        function handleFiles(files) {
            if (files.length > 0) {
                const file = files[0];
                const validTypes = ['.pdf', '.doc', '.docx'];
                const fileExtension = file.name.substring(file.name.lastIndexOf('.')).toLowerCase();
                
                if (!validTypes.includes(fileExtension)) {
                    showFeedback('Invalid file type. Please upload a PDF or DOCX file.', 'error');
                    resetFileInput();
                    return;
                }

                const dataTransfer = new DataTransfer();
                dataTransfer.items.add(file);
                fileInput.files = dataTransfer.files;

                fileNameDisplay.textContent = file.name;
                dropArea.style.display = 'none';
                fileInfo.style.display = 'flex';
            }
        }

        removeFileBtn.addEventListener('click', () => resetFileInput());

        function resetFileInput() {
            fileInput.value = '';
            dropArea.style.display = 'block';
            fileInfo.style.display = 'none';
        }
        
        return { resetFileInput };
    }

    const resumeZone = setupDropZone('drop-area-resume', 'file-resume', 'file-info-resume', 'file-name-resume', 'remove-file-resume');
    const jdZone = setupDropZone('drop-area-jd', 'file-jd', 'file-info-jd', 'file-name-jd', 'remove-file-jd');

    function showFeedback(message, type) {
        feedbackMsg.textContent = message;
        feedbackMsg.className = `feedback-message ${type} show`;
        feedbackMsg.style.display = 'block';
        setTimeout(() => {
            feedbackMsg.classList.remove('show');
            setTimeout(() => {
                if(!feedbackMsg.classList.contains('show')) feedbackMsg.style.display = 'none';
            }, 300);
        }, 5000);
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const resumeFile = document.getElementById('file-resume').files[0];
        const jdFile = document.getElementById('file-jd').files[0];

        if (!resumeFile || !jdFile) {
            showFeedback('Please provide both Resume and Job Description.', 'error');
            return;
        }

        const formData = new FormData();
        formData.append('resumeFile', resumeFile);
        formData.append('jdFile', jdFile);

        submitBtn.classList.add('loading');
        submitBtn.disabled = true;
        timeline.innerHTML = '';

        try {
            const response = await fetch('/process', {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                const rawText = await response.text();
                let responseBody;
                try {
                    responseBody = JSON.parse(rawText);
                } catch(e) {
                    responseBody = { csv: rawText };
                }
                
                showFeedback('Upload successful! Roadmap Generated.', 'success');
                
                if (responseBody.metrics) {
                    document.getElementById('metrics-dashboard').style.display = 'grid';
                    document.getElementById('metric-match').textContent = responseBody.metrics.match_percentage + "%";
                    document.getElementById('metric-missing').textContent = responseBody.metrics.missing_skills_count;
                    document.getElementById('metric-courses').textContent = responseBody.metrics.courses_required;
                }
                
                renderRoadmap(responseBody.csv || responseBody);
            } else {
                throw new Error('Upload failed with status: ' + response.status);
            }
        } catch (error) {
            console.error('Submission Error:', error);
            showFeedback('Failed to process documents. Please try again.', 'error');
        } finally {
            submitBtn.classList.remove('loading');
            submitBtn.disabled = false;
        }
    });

    function renderRoadmap(csvString) {
        if (!csvString || csvString.includes("Failed") || csvString.includes("Error")) {
            showFeedback(csvString || "Failed to extract roadmap properly.", "error");
            return;
        }
        const lines = csvString.trim().split('\n');
        if (lines.length <= 1) {
            showFeedback("Invalid Roadmap data format.", "error");
            return;
        }
        
        function parseCSVLine(line) {
            const result = [];
            let currentStr = '';
            let inQuotes = false;
            for (let i = 0; i < line.length; i++) {
                const char = line[i];
                if (char === '"') {
                    inQuotes = !inQuotes;
                } else if (char === ',' && !inQuotes) {
                    result.push(currentStr.trim().replace(/^"|"$/g, ''));
                    currentStr = '';
                } else {
                    currentStr += char;
                }
            }
            result.push(currentStr.trim().replace(/^"|"$/g, ''));
            return result;
        }

        const dataLines = lines.slice(1);
        dataLines.forEach((line, index) => {
            if (!line.trim()) return;
            const match = parseCSVLine(line);
            if (match && match.length >= 3) {
                const title = match[0];
                const desc = match[1];
                const duration = match[2];
                const reasoning = match.length > 3 ? match[3] : "Standard curriculum progression.";
                
                let node = document.createElement('div');
                node.className = 'timeline-item fade-in';
                node.style.animationDelay = `${index * 0.15}s`;
                
                node.innerHTML = `
                    <div class="timeline-marker">
                        <div class="marker-dot"></div>
                        <div class="marker-line"></div>
                    </div>
                    <div class="timeline-content glass-effect">
                        <div class="timeline-header">
                            <span class="timeline-phase">Phase ${index + 1}</span>
                            <span class="timeline-duration">${duration}</span>
                        </div>
                        <h3>${title}</h3>
                        <p>${desc}</p>
                        <div class="reasoning-trace">
                            <strong>AI Reasoning:</strong> ${reasoning}
                        </div>
                    </div>
                `;
                timeline.appendChild(node);
            }
        });

    }
});
