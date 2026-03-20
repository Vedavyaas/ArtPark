document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('ingest-form');
    const dropArea = document.getElementById('drop-area');
    const fileInput = document.getElementById('file');
    const fileInfo = document.getElementById('file-info');
    const fileNameDisplay = document.getElementById('file-name');
    const removeFileBtn = document.getElementById('remove-file');
    const submitBtn = document.getElementById('submit-btn');
    const feedbackMsg = document.getElementById('feedback-message');

    // Make the entire drop area clickable
    dropArea.addEventListener('click', () => {
        fileInput.click();
    });

    // Handle drag and drop events
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
        dropArea.addEventListener(eventName, () => {
            dropArea.classList.add('dragover');
        }, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, () => {
            dropArea.classList.remove('dragover');
        }, false);
    });

    dropArea.addEventListener('drop', (e) => {
        const dt = e.dataTransfer;
        const files = dt.files;
        handleFiles(files);
    });

    fileInput.addEventListener('change', function() {
        handleFiles(this.files);
    });

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

            // Assign dragged file back to input so it's submitted with form automatically
            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(file);
            fileInput.files = dataTransfer.files;

            fileNameDisplay.textContent = file.name;
            dropArea.style.display = 'none';
            fileInfo.style.display = 'flex';
        }
    }

    removeFileBtn.addEventListener('click', () => {
        resetFileInput();
    });

    function resetFileInput() {
        fileInput.value = '';
        dropArea.style.display = 'block';
        fileInfo.style.display = 'none';
    }

    function showFeedback(message, type) {
        feedbackMsg.textContent = message;
        feedbackMsg.className = `feedback-message ${type} show`;
        
        setTimeout(() => {
            feedbackMsg.classList.remove('show');
            setTimeout(() => {
                if(!feedbackMsg.classList.contains('show')) {
                    feedbackMsg.style.display = 'none';
                }
            }, 300);
        }, 5000);
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const file = fileInput.files[0];
        const name = document.getElementById('name').value.trim();

        if (!file || !name) {
            showFeedback('Please provide both name and resume.', 'error');
            return;
        }

        const formData = new FormData();
        formData.append('name', name);
        formData.append('file', file);

        submitBtn.classList.add('loading');
        submitBtn.disabled = true;

        try {
            const response = await fetch('/ingest/resume', {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                const resultText = await response.text();
                showFeedback(resultText || 'Upload successful!', 'success');
                form.reset();
                resetFileInput();
            } else {
                throw new Error('Upload failed with status: ' + response.status);
            }
        } catch (error) {
            console.error('Submission Error:', error);
            showFeedback('Failed to ingest resume. Please try again.', 'error');
        } finally {
            submitBtn.classList.remove('loading');
            submitBtn.disabled = false;
        }
    });
});
