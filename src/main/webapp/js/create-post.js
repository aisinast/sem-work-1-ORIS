document.addEventListener('DOMContentLoaded', function() {
    setupPhotoInput();
    setupTagsSelection();
});

function setupPhotoInput() {
    const photoInput = document.getElementById('photo');
    const photoName = document.getElementById('photoName');
    if (photoInput) {
        photoInput.addEventListener('change', () => {
            photoName.textContent = photoInput.files?.[0]?.name || 'Файл не выбран';
        });
    }
}

function setupTagsSelection() {
    const tagsStrip = document.getElementById('tagsStrip');
    const tagIds = document.getElementById('tagIds');
    const selected = new Set();

    if (tagsStrip && tagIds) {
        tagsStrip.addEventListener('click', (e) => {
            const el = e.target.closest('.tag');
            if (!el) return;
            const id = el.dataset.tagId;
            if (selected.has(id)) {
                selected.delete(id);
                el.classList.remove('active');
            } else {
                selected.add(id);
                el.classList.add('active');
            }
            tagIds.value = Array.from(selected).join(',');
        });

        function presetTags() {
            const presetVal = tagIds.value;
            if (!presetVal) return;
            const ids = new Set(presetVal.split(',').filter(Boolean));
            document.querySelectorAll('.tag').forEach(el => {
                if (ids.has(el.dataset.tagId)) {
                    el.classList.add('active');
                    selected.add(el.dataset.tagId);
                }
            });
        }

        presetTags();
    }
}

