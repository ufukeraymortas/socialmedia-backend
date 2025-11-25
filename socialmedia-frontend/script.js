document.addEventListener('DOMContentLoaded', () => {

    // --- AYARLAR ---
    const API_URL = 'https://senato.onrender.com'; // Backend adresiniz

    // --- ELEMENTLER ---
    // Konteynerler
    const authContainer = document.getElementById('auth-container');
    const mainAppContainer = document.getElementById('main-app-container');
    const feedView = document.getElementById('feed-view');
    const profileView = document.getElementById('profile-view');

    // Formlar
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const postForm = document.getElementById('post-form');
    
    // Listeler
    const postsContainer = document.getElementById('posts-container'); // Ana akış
    const profileContentContainer = document.getElementById('profile-content-container'); // Profil akışı

    // Profil Sayfası Elementleri
    const profileUsernameEl = document.getElementById('profile-username');
    const profileTitleEl = document.getElementById('profile-title');
    const profileBioEl = document.getElementById('profile-bio');
    const profilePictureEl = document.getElementById('profile-picture');
    const editProfileBtn = document.getElementById('edit-profile-btn');
    const editProfileForm = document.getElementById('edit-profile-form');
    const backToFeedBtn = document.getElementById('back-to-feed-btn');
    const myProfileBtn = document.getElementById('my-profile-btn');
    const logoutBtn = document.getElementById('logout-btn');

    // Durum Değişkenleri
    let currentUserId = null;
    let currentUsername = null;

    // ---------------------------------------------------------
    // 1. TEMEL GÖSTER/GİZLE FONKSİYONLARI
    // ---------------------------------------------------------

    function showApp(user) {
        currentUserId = user.id;
        currentUsername = user.username;
        
        authContainer.classList.add('hidden');
        mainAppContainer.classList.remove('hidden');
        
        // Uygulama açılınca Ana Akışı göster
        showFeed();
    }

    function showFeed() {
        profileView.classList.add('hidden');
        feedView.classList.remove('hidden');
        loadGlobalPosts(); // Tüm postları yükle
    }

    // --- PROFİL SAYFASINI AÇAN KRİTİK FONKSİYON ---
    // Bu fonksiyonu global yapıyoruz ki HTML içinden çağırabilelim
    window.openProfile = async function(userId) {
        // 1. Görünümü değiştir
        feedView.classList.add('hidden');
        profileView.classList.remove('hidden');
        
        // 2. Profil başlığını doldur
        await loadUserProfileHeader(userId);
        
        // 3. O kişinin postlarını yükle
        await loadUserPosts(userId);

        // 4. Eğer kendi profilimse "Düzenle" butonunu göster
        if (userId === currentUserId) {
            editProfileBtn.classList.remove('hidden');
        } else {
            editProfileBtn.classList.add('hidden');
            editProfileForm.classList.add('hidden'); // Başkasının profilinde formu gizle
        }
    };

    function showAuth() {
        currentUserId = null;
        currentUsername = null;
        mainAppContainer.classList.add('hidden');
        authContainer.classList.remove('hidden');
        loginForm.reset();
    }

    // ---------------------------------------------------------
    // 2. VERİ ÇEKME (FETCH) FONKSİYONLARI
    // ---------------------------------------------------------

    // A. Global Akış (Herkesin Postları)
    async function loadGlobalPosts() {
        postsContainer.innerHTML = '<p style="text-align:center;">Yükleniyor...</p>';
        try {
            const res = await fetch(`${API_URL}/api/posts`);
            const posts = await res.json();
            renderPosts(posts, postsContainer);
        } catch (error) {
            console.error(error);
        }
    }

    // B. Kişi Profili (Sadece O Kişinin Postları)
    async function loadUserPosts(userId) {
        profileContentContainer.innerHTML = '<p style="text-align:center;">Yükleniyor...</p>';
        try {
            // Backend'e eklediğimiz yeni API: /api/posts/user/{userId}
            const res = await fetch(`${API_URL}/api/posts/user/${userId}`);
            const posts = await res.json();
            
            if (posts.length === 0) {
                profileContentContainer.innerHTML = '<p style="text-align:center; margin-top:20px;">Henüz gönderi yok.</p>';
            } else {
                renderPosts(posts, profileContentContainer);
            }
        } catch (error) {
            console.error(error);
        }
    }

    // C. Profil Başlık Bilgileri (Foto, Bio, Ünvan)
    async function loadUserProfileHeader(userId) {
        try {
            const res = await fetch(`${API_URL}/api/users/${userId}`);
            const user = await res.json();

            profileUsernameEl.textContent = user.username;
            profileTitleEl.textContent = user.title || 'Ünvan Yok';
            profileBioEl.textContent = user.bio || 'Biyografi yok.';
            
            if (user.profilePictureUrl) {
                profilePictureEl.src = `${API_URL}/uploads/${user.profilePictureUrl}`;
            } else {
                profilePictureEl.src = 'https://via.placeholder.com/100';
            }
            
            // Düzenleme formu için inputları hazırla (kendi profilimse)
            if (userId === currentUserId) {
                document.getElementById('edit-title-input').value = user.title || '';
                document.getElementById('edit-bio-input').value = user.bio || '';
            }

        } catch (error) {
            console.error('Profil bilgisi alınamadı:', error);
        }
    }

    // ---------------------------------------------------------
    // 3. ORTAK HTML OLUŞTURUCU (RENDERER)
    // ---------------------------------------------------------
    
    // Bu fonksiyon hem ana akış hem de profil akışı için kullanılır
    async function renderPosts(posts, container) {
        container.innerHTML = '';

        for (const post of posts) {
            const postEl = document.createElement('div');
            postEl.className = 'post';
            
            // Medya
            let mediaHtml = '';
            if (post.imageUrl) mediaHtml = `<img src="${API_URL}/uploads/${post.imageUrl}" class="post-image">`;
            else if (post.videoUrl) mediaHtml = `<video src="${API_URL}/uploads/${post.videoUrl}" class="post-video" controls></video>`;

            // Beğeni Durumu
            const likeInfo = await checkLikeStatus(post.id);

            // HTML Şablonu
            // DİKKAT: İsim ve Resim kısmına onclick="openProfile(...)" ekliyoruz!
            // event.stopPropagation() postun geneline tıklanırsa tetiklenmesin diye butonlara eklenir.
            postEl.innerHTML = `
                <div class="post-header" style="cursor:pointer;" onclick="openProfile(${post.author.id})">
                    <strong style="color:#0563bb; font-size:16px;">${post.author.username}</strong> 
                    <small style="color:#888; margin-left:5px;">${new Date(post.timestamp).toLocaleString()}</small>
                </div>
                
                <div class="post-content" style="margin-top:5px;">${post.content}</div>
                ${mediaHtml}
                
                <div class="post-actions">
                    <button class="like-btn ${likeInfo.isLiked ? 'liked' : ''}" 
                            data-post-id="${post.id}" onclick="event.stopPropagation()">
                        ${likeInfo.isLiked ? '❤️' : '🤍'} <span id="count-${post.id}">${likeInfo.count}</span>
                    </button>
                </div>

                <div class="comments-section">
                    <div class="comments-list" id="comments-list-${post.id}"></div>
                    <form class="comment-form" data-post-id="${post.id}" onclick="event.stopPropagation()">
                        <input type="text" placeholder="Yorum yaz..." style="width:70%" required>
                        <button type="submit">Gönder</button>
                    </form>
                </div>
            `;
            
            container.appendChild(postEl);
            
            // Beğeni butonu listener
            postEl.querySelector('.like-btn').addEventListener('click', (e) => toggleLike(e.currentTarget));
            
            // Yorum formu listener
            postEl.querySelector('.comment-form').addEventListener('submit', (e) => handleCommentSubmit(e));

            // Yorumları yükle
            loadComments(post.id);
        }
    }

    // ---------------------------------------------------------
    // 4. OLAY DİNLEYİCİLERİ (BUTTON CLICKS)
    // ---------------------------------------------------------

    // Navigasyon Butonları
    backToFeedBtn.addEventListener('click', showFeed);
    myProfileBtn.addEventListener('click', () => openProfile(currentUserId));
    logoutBtn.addEventListener('click', showAuth);

    // Profil Düzenleme
    editProfileBtn.addEventListener('click', () => {
        document.getElementById('edit-profile-form').classList.remove('hidden');
    });
    
    document.getElementById('cancel-edit-btn').addEventListener('click', () => {
        document.getElementById('edit-profile-form').classList.add('hidden');
    });

    document.getElementById('edit-profile-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('title', document.getElementById('edit-title-input').value);
        formData.append('bio', document.getElementById('edit-bio-input').value);
        const file = document.getElementById('edit-pp-input').files[0];
        if (file) formData.append('profilePicture', file);

        try {
            const res = await fetch(`${API_URL}/api/users/${currentUserId}/profile`, {
                method: 'PUT', body: formData
            });
            if (res.ok) {
                alert('Profil güncellendi!');
                document.getElementById('edit-profile-form').classList.add('hidden');
                loadUserProfileHeader(currentUserId); // Başlığı yenile
            }
        } catch (err) { alert('Hata'); }
    });

    // Post Atma
    postForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('content', document.getElementById('post-content').value);
        formData.append('authorId', currentUserId);
        const file = document.getElementById('post-file').files[0];
        if (file) formData.append('file', file);

        const res = await fetch(`${API_URL}/api/posts`, { method: 'POST', body: formData });
        if (res.ok) {
            postForm.reset();
            loadGlobalPosts(); // Akışı yenile
        }
    });

    // --- YARDIMCI API FONKSİYONLARI (Beğeni, Yorum vb.) ---
    
    async function checkLikeStatus(postId) {
        try {
            const res = await fetch(`${API_URL}/api/posts/${postId}/likes?userId=${currentUserId}`);
            return await res.json();
        } catch { return { count: 0, isLiked: false }; }
    }

    async function toggleLike(btn) {
        const postId = btn.dataset.postId;
        const res = await fetch(`${API_URL}/api/posts/${postId}/likes?userId=${currentUserId}`, { method: 'POST' });
        if (res.ok) {
            const newStatus = await checkLikeStatus(postId);
            btn.className = `like-btn ${newStatus.isLiked ? 'liked' : ''}`;
            btn.innerHTML = `${newStatus.isLiked ? '❤️' : '🤍'} <span>${newStatus.count}</span>`;
        }
    }

    async function loadComments(postId) {
        const container = document.getElementById(`comments-list-${postId}`);
        const res = await fetch(`${API_URL}/api/posts/${postId}/comments`);
        const comments = await res.json();
        container.innerHTML = comments.map(c => `
            <div style="font-size:12px; margin-top:5px; border-left:2px solid #ddd; padding-left:5px;">
                <b>${c.author.username}:</b> ${c.content}
            </div>
        `).join('');
    }

    async function handleCommentSubmit(e) {
        e.preventDefault();
        const form = e.target;
        const postId = form.dataset.postId;
        const content = form.querySelector('input').value;
        
        const formData = new FormData();
        formData.append('content', content);
        formData.append('authorId', currentUserId);
        
        const res = await fetch(`${API_URL}/api/posts/${postId}/comments`, { method: 'POST', body: formData });
        if(res.ok) {
            form.reset();
            loadComments(postId);
        }
    }

    // Giriş/Kayıt (Eski koddan aynen)
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const u = document.getElementById('login-username').value;
        const p = document.getElementById('login-password').value;
        const res = await fetch(`${API_URL}/api/users/login`, {
            method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({username:u, password:p})
        });
        if(res.ok) showApp(await res.json()); else alert('Hata');
    });

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const u = document.getElementById('register-username').value;
        const p = document.getElementById('register-password').value;
        const res = await fetch(`${API_URL}/api/users/register`, {
            method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({username:u, password:p})
        });
        if(res.ok) { alert('Kayıt Başarılı'); showApp(await res.json()); } else alert('Hata');
    });

    // Tab Geçişleri
    document.getElementById('show-login-btn').onclick = () => { loginForm.classList.remove('hidden'); registerForm.classList.add('hidden'); };
    document.getElementById('show-register-btn').onclick = () => { loginForm.classList.add('hidden'); registerForm.classList.remove('hidden'); };

});