document.addEventListener('DOMContentLoaded', function() {
    initPhotoInput();
    initTags();
});

function initPhotoInput() {
    const photoInput = document.getElementById('photo');
    const photoName = document.getElementById('photoName');

    if (photoInput && photoName) {
        photoInput.addEventListener('change', () => {
            photoName.textContent = photoInput.files?.[0]?.name || 'Файл не выбран';
        });
    }
}

function initTags() {
    const tagsStrip = document.getElementById('tagsStrip');
    const tagIds = document.getElementById('tagIds');

    if (!tagsStrip || !tagIds) return;

    const selected = new Set((tagIds.value || '').split(',').filter(Boolean));

    document.querySelectorAll('.tag').forEach(el => {
        if (selected.has(el.dataset.tagId)) {
            el.classList.add('active');
        }
    });

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
}
