document.addEventListener('DOMContentLoaded', function() {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            const contextPath = this.getAttribute('data-context-path');
            logout(contextPath);
        });
    }
});

async function logout(contextPath) {
    if (!confirm("Вы уверены, что хотите выйти?")) {
        return;
    }

    await fetch(contextPath + '/logout', {
        method: 'GET'
    });

    window.location.href = contextPath + '/main-page';
}