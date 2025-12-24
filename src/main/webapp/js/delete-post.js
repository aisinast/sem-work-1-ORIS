async function deletePost(contextPath, postId) {
    if (!confirm("Вы уверены, что хотите удалить этот пост?")) {
        return;
    }

    try {
        const res = await fetch(`${contextPath}/posts/${postId}`, {
            method: 'DELETE'
        });

        if (res.ok) {
            window.location.href = `${contextPath}/main-page`;
        } else {
            const errorText = await res.text();
            console.error('Delete failed:', res.status, errorText);

            if (res.status === 403) {
                alert('У вас нет прав для удаления этого поста');
            } else if (res.status === 404) {
                alert('Пост не найден');
            } else {
                alert('Не удалось удалить пост');
            }
        }
    } catch (error) {
        console.error('Delete error:', error);
        alert('Ошибка сети при удалении поста');
    }
}