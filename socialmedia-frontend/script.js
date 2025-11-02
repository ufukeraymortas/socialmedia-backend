// Sayfa ilk yüklendiğinde bu kodun çalışmasını bekle
document.addEventListener('DOMContentLoaded', () => {

    // Backend (Java) sunucumuzun adresi
    const API_URL = 'https://senato.onrender.com';

    // HTML'deki BÖLÜMLERİ yakalıyoruz
    const authContainer = document.getElementById('auth-container');
    const mainAppContainer = document.getElementById('main-app-container');

    // HTML'deki FORMLARI yakalıyoruz
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const postForm = document.getElementById('post-form');
    
    // HTML'deki BUTONLARI ve diğer elementleri yakalıyoruz
    const showLoginBtn = document.getElementById('show-login-btn');
    const showRegisterBtn = document.getElementById('show-register-btn');
    const logoutBtn = document.getElementById('logout-btn');
    const postsContainer = document.getElementById('posts-container');
    const currentUserInfo = document.getElementById('current-user-info');

    // Kullanıcı bilgilerini saklayacağımız yer
    let currentUserId = null;
    let currentUsername = null;

    // --- YARDIMCI FONKSİYONLAR (GÖSTER/GİZLE) ---

    function showApp(user) {
        currentUserId = user.id;
        currentUsername = user.username;
        currentUserInfo.textContent = `Hoş geldin, ${currentUsername}!`;
        authContainer.classList.add('hidden');
        mainAppContainer.classList.remove('hidden');
        fetchPosts(); // GİRİŞ YAPINCA POSTLARI YÜKLE
    }

    function showAuth() {
        currentUserId = null;
        currentUsername = null;
        mainAppContainer.classList.add('hidden');
        authContainer.classList.remove('hidden');
        postsContainer.innerHTML = ''; // Çıkış yapınca postları temizle
        loginForm.reset();
        registerForm.reset();
    }

    // --- FORM GEÇİŞLERİ (Giriş <-> Kayıt Sekmeleri) ---

    showLoginBtn.addEventListener('click', () => {
        loginForm.classList.remove('hidden');
        registerForm.classList.add('hidden');
        showLoginBtn.classList.add('active');
        showRegisterBtn.classList.remove('active');
    });

    showRegisterBtn.addEventListener('click', () => {
        loginForm.classList.add('hidden');
        registerForm.classList.remove('hidden');
        showLoginBtn.classList.remove('active');
        showRegisterBtn.classList.add('active');
    });

    // --- ANA API FONKSİYONLARI ---

    // 1. GİRİŞ YAPMA (Değişiklik yok)
    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;
        try {
            const response = await fetch(`${API_URL}/api/users/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: username, password: password })
            });
            if (!response.ok) throw new Error('Kullanıcı adı veya şifre hatalı!');
            const user = await response.json();
            showApp(user);
        } catch (error) {
            alert(error.message);
        }
    });

    // 2. KAYIT OLMA (Değişiklik yok)
    registerForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const username = document.getElementById('register-username').value;
        const password = document.getElementById('register-password').value;
        try {
            const response = await fetch(`${API_URL}/api/users/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: username, password: password })
            });
            if (!response.ok) throw new Error('Kayıt başarısız. Kullanıcı adı alınmış olabilir.');
            const user = await response.json();
            alert('Başarıyla kayıt oldunuz! Akışa yönlendiriliyorsunuz.');
            showApp(user);
        } catch (error) {
            alert(error.message);
        }
    });

    // 3. ÇIKIŞ YAPMA (Değişiklik yok)
    logoutBtn.addEventListener('click', () => {
        showAuth();
    });

    // 4. POST OLUŞTURMA (Değişiklik yok)
    postForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!currentUserId) {
            alert('Post atmak için giriş yapmış olmalısınız.');
            return;
        }
        const content = document.getElementById('post-content').value;
        const fileInput = document.getElementById('post-file');
        const file = fileInput.files[0];
        const formData = new FormData();
        formData.append('content', content);
        formData.append('authorId', currentUserId);
        if (file) {
            formData.append('file', file);
        }
        try {
            const response = await fetch(`${API_URL}/api/posts`, {
                method: 'POST',
                body: formData
            });
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error('Post oluşturulamadı. Hata: ' + errorText);
            }
            postForm.reset();
            fileInput.value = null;
            await fetchPosts(); // TÜM AKIŞI YENİLE
        } catch (error) {
            alert(error.message);
        }
    });

    // 5. TÜM POSTLARI GETİRME (GÜNCELLENDİ)
    async function fetchPosts() {
        try {
            const response = await fetch(`${API_URL}/api/posts`);
            const posts = await response.json();
            postsContainer.innerHTML = ''; // Listeyi temizle

            // Her post için HTML oluştur
            for (const post of posts) {
                const postElement = document.createElement('div');
                postElement.className = 'post';
                
                // Medya (foto/video) HTML'ini oluştur
                let mediaHtml = '';
                if (post.imageUrl) {
                    mediaHtml = `<img src="${API_URL}/uploads/${post.imageUrl}" alt="Post Resmi" class="post-image">`;
                } else if (post.videoUrl) {
                    mediaHtml = `<video src="${API_URL}/uploads/${post.videoUrl}" class="post-video" controls width="100%"></video>`;
                }
                
                // YORUM BÖLÜMÜ HTML'İ (GÜNCELLENDİ)
                postElement.innerHTML = `
                    <div class="post-author">${post.author.username}</div>
                    <div class="post-content">${post.content}</div>
                    ${mediaHtml}
                    <div class="post-timestamp">${new Date(post.timestamp).toLocaleString('tr-TR')}</div>
                    
                    <div class="comments-section">
                        <h4>Yorumlar</h4>
                        <div class="comments-list" id="comments-for-post-${post.id}">
                            </div>
                        
                        <form class="comment-form" data-post-id="${post.id}">
                            
                            <div class="replying-to-info" id="reply-info-for-post-${post.id}">
                                <span></span>
                                <button type="button" class="cancel-reply-btn">[İptal]</button>
                            </div>
                            
                            <textarea placeholder="Yorumun..." required></textarea>
                            <input type="file" accept="image/*,video/*">
                            <button type="submit">Yorum Yap</button>
                        </form>
                    </div>
                `;
                
                postsContainer.appendChild(postElement);

                // Postu ekrana bastıktan HEMEN SONRA o postun ana yorumlarını yükle
                await loadComments(post.id); 
            }
        } catch (error) {
            console.error('Postlar alınamadı:', error);
        }
    }
    
    // 6. YORUMLARI GETİRME (GÜNCELLENDİ - Artık İç İçe)
    async function loadComments(postId) {
        try {
            // API'miz artık SADECE ANA YORUMLARI getiriyor
            const response = await fetch(`${API_URL}/api/posts/${postId}/comments`);
            const topLevelComments = await response.json();
            
            const commentsListContainer = document.getElementById(`comments-for-post-${postId}`);
            if (!commentsListContainer) return;
            
            commentsListContainer.innerHTML = ''; // Yorum listesini temizle
            
            // Yorumları (ve cevaplarını) ekrana çizmek için yeni fonksiyonu çağır
            renderCommentsRecursive(topLevelComments, commentsListContainer);
            
        } catch (error) {
            console.error(`Post ${postId} için yorumlar alınamadı:`, error);
        }
    }
    
    // 7. YORUMLARI VE CEVAPLARI EKLEME (YENİ FONKSİYON)
    // Bu fonksiyon, kendini tekrar çağıran (recursive) bir fonksiyondur.
    function renderCommentsRecursive(comments, containerElement) {
        comments.forEach(comment => {
            const commentElement = document.createElement('div');
            commentElement.className = 'comment';
            
            // Yorumlar için de medya HTML'i oluştur
            let mediaHtml = '';
            if (comment.imageUrl) {
                mediaHtml = `<img src="${API_URL}/uploads/${comment.imageUrl}" alt="Yorum Resmi" class="comment-image">`;
            } else if (comment.videoUrl) {
                mediaHtml = `<video src="${API_URL}/uploads/${comment.videoUrl}" class="comment-video" controls width="100%"></video>`;
            }
            
            // YENİ: Yorum HTML'i artık 'Cevapla' butonu ve cevaplar için
            // boş bir konteyner içeriyor.
            commentElement.innerHTML = `
                <div class="comment-main">
                    <span class="comment-author">${comment.author.username}:</span>
                    <span class="comment-content">
                        ${comment.content}
                        ${mediaHtml}
                    </span>
                    <span class="comment-timestamp">${new Date(comment.timestamp).toLocaleTimeString('tr-TR')}</span>
                </div>
                
                <div class="comment-actions">
                    <button type="button" class="reply-btn" 
                            data-comment-id="${comment.id}" 
                            data-author-username="${comment.author.username}">
                        Cevapla
                    </button>
                </div>
                
                <div class="replies-container" id="replies-for-comment-${comment.id}">
                    </div>
            `;
            
            containerElement.appendChild(commentElement);
            
            // İÇ İÇE (RECURSION) KISMI:
            // Eğer bu yorumun 'replies' (cevaplar) listesi varsa...
            if (comment.replies && comment.replies.length > 0) {
                // ...o cevaplar için bu fonksiyonu TEKRAR ÇAĞIR.
                // Ama bu kez konteyner olarak az önce oluşturduğumuz
                // 'replies-container'ı (cevap konteynerını) ver.
                const repliesContainer = document.getElementById(`replies-for-comment-${comment.id}`);
                renderCommentsRecursive(comment.replies, repliesContainer);
            }
        });
    }

    // 8. YORUM GÖNDERME (GÜNCELLENDİ - Artık Cevaplamayı Biliyor)
    postsContainer.addEventListener('submit', async (event) => {
        if (!event.target.classList.contains('comment-form')) {
            return;
        }
        event.preventDefault();
        
        const form = event.target;
        const postId = form.dataset.postId;
        
        // YENİ: Ana yorumun ID'sini formun 'data'sından oku
        const parentCommentId = form.dataset.parentCommentId;
        
        if (!currentUserId || !postId) {
            alert('Yorum yapmak için giriş yapmış olmalısınız.');
            return;
        }

        const content = form.querySelector('textarea').value;
        const fileInput = form.querySelector('input[type="file"]');
        const file = fileInput.files[0];

        const formData = new FormData();
        formData.append('content', content);
        formData.append('authorId', currentUserId);
        
        // YENİ: Eğer bir ana yoruma cevap veriyorsak,
        // o ID'yi de FormData'ya ekle
        if (parentCommentId) {
            formData.append('parentCommentId', parentCommentId);
        }
        
        if (file) {
            formData.append('file', file);
        }
        
        try {
            // API adresi aynı: /api/posts/{postId}/comments
            const response = await fetch(`${API_URL}/api/posts/${postId}/comments`, {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error('Yorum oluşturulamadı. Hata: ' + errorText);
            }

            // Başarılı! Formu ve cevaplama durumunu sıfırla
            form.reset();
            fileInput.value = null;
            form.dataset.parentCommentId = ''; // Cevaplama durumunu sıfırla
            form.querySelector('.replying-to-info').style.display = 'none';
            
            // O postun tüm yorumlarını (yeni cevapla birlikte) yenile
            await loadComments(postId);

        } catch (error) {
            alert(error.message);
        }
    });
    
    // 9. TIKLAMA OLAYLARI (YENİ EVENT LISTENER)
    // Bu, "Cevapla" ve "İptal" butonlarını yakalayacak
    postsContainer.addEventListener('click', (event) => {
        
        // --- "CEVAPLA" BUTONUNA TIKLANDIYSA ---
        if (event.target.classList.contains('reply-btn')) {
            const button = event.target;
            const commentId = button.dataset.commentId;
            const authorName = button.dataset.authorUsername;
            
            // Postun içindeki ana yorum formunu bul
            const postElement = button.closest('.post');
            const commentForm = postElement.querySelector('.comment-form');
            
            // O formun içindeki "reply-info" kutusunu bul
            const replyInfo = commentForm.querySelector('.replying-to-info');
            
            // Yorum formuna, hangi yoruma cevap verdiğini 'data' olarak kaydet
            commentForm.dataset.parentCommentId = commentId;
            
            // "reply-info" kutusunu doldur ve göster
            replyInfo.querySelector('span').textContent = `@${authorName} kişisine cevap veriliyor...`;
            replyInfo.style.display = 'block';
            
            // Cevap yazması için kullanıcıyı forma odaklansın
            commentForm.querySelector('textarea').focus();
        }
        
        // --- "İPTAL" BUTONUNA TIKLANDIYSA ---
        if (event.target.classList.contains('cancel-reply-btn')) {
            const button = event.target;
            
            // Yorum formunu bul
            const commentForm = button.closest('.comment-form');
            
            // "reply-info" kutusunu bul
            const replyInfo = button.closest('.replying-to-info');
            
            // Yorum formundaki 'parentCommentId'yi sıfırla
            commentForm.dataset.parentCommentId = '';
            
            // "reply-info" kutusunu gizle
            replyInfo.style.display = 'none';
        }
    });

    // --- SAYFA AÇILDIĞINDA İLK YAPILACAK İŞ ---
    // (Değişmedi)
});