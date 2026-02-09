/* Hytale Server Documentation - Shared JavaScript */

// ── Search ──────────────────────────────────────────────

const searchIndex = [
    { title: "Overview", url: "index.html", keywords: "home overview architecture universe entity store component" },
    { title: "Getting Started", url: "getting-started.html", keywords: "setup install plugin gradle tutorial first steps hello world" },
    { title: "ECS Architecture", url: "ecs.html", keywords: "entity component system store holder ref archetype componenttype query" },
    { title: "Threading Model", url: "threading.html", keywords: "thread tick tps tickingthread forkjoin stampedlock concurrent golden rule" },
    { title: "Networking", url: "networking.html", keywords: "packet protocol quic tcp netty transport servermanager connection handler" },
    { title: "Worlds & Universe", url: "worlds.html", keywords: "world universe chunk create instance spawn provider tickingthread" },
    { title: "Event System", url: "events.html", keywords: "event eventbus listener handler priority cancellable dispatch register" },
    { title: "Command System", url: "commands.html", keywords: "command commandbase argument argtype register execute permission" },
    { title: "Permissions", url: "permissions.html", keywords: "permission group operator op holder provider role hierarchy" },
    { title: "Teleportation", url: "teleportation.html", keywords: "teleport position rotation velocity cross-world transfer dimension" },
    { title: "Blocks", url: "blocks.html", keywords: "block blocktype blockstate palette section chunk lighting ticking rotation filler" },
    { title: "World Generation", url: "worldgen.html", keywords: "worldgen biome terrain cave prefab climate zone noise generator" },
    { title: "UI & HUD", url: "ui.html", keywords: "ui hud title notification toast anchor area page custom browser" },
    { title: "Sound & Audio", url: "sound.html", keywords: "sound audio 2d 3d play category volume pitch ambient" },
    { title: "Entities & NPCs", url: "entities.html", keywords: "entity spawn despawn livingentity blockentity tracker network visibility lod" },
    { title: "NPC & AI", url: "npc.html", keywords: "npc ai behavior blackboard decision sensor action instruction pathfinding navigation astar" },
    { title: "Players", url: "players.html", keywords: "player playerref inventory hotbar gamemode connect disconnect data uuid" },
    { title: "Items & Inventory", url: "items.html", keywords: "item itemstack inventory container hotbar armor durability crafting recipe" },
    { title: "Damage & Combat", url: "combat.html", keywords: "damage health death respawn knockback armor effect stat modifier damagecause" },
    { title: "Plugin Development", url: "plugins.html", keywords: "plugin javaplugin manifest gradle setup lifecycle registry event command" },
    { title: "Configuration", url: "configuration.html", keywords: "config server port auth backup transport maxplayers settings" },
    { title: "Data Packs", url: "data-packs.html", keywords: "data pack resource asset assetstore codec json hot reload mod content block item sound" },
    { title: "ECS Catalog", url: "reference.html", keywords: "reference catalog component list type all tracker physics projectile nameplate" },
    { title: "Match System Design", url: "match-system-design.html", keywords: "match matchmaking pvp competitive lobby ready queue pending active ended condition handler lifecycle transition player state" },
];

function openSearch() {
    const overlay = document.querySelector('.search-overlay');
    if (!overlay) return;
    overlay.classList.add('active');
    const input = overlay.querySelector('.search-input');
    if (input) { input.value = ''; input.focus(); }
    updateSearchResults('');
}

function closeSearch() {
    const overlay = document.querySelector('.search-overlay');
    if (overlay) overlay.classList.remove('active');
}

function updateSearchResults(query) {
    const container = document.querySelector('.search-results');
    if (!container) return;
    if (!query.trim()) {
        container.innerHTML = '<div class="search-empty">Type to search documentation...</div>';
        return;
    }
    const q = query.toLowerCase();
    const results = searchIndex.filter(item =>
        item.title.toLowerCase().includes(q) || item.keywords.includes(q)
    );
    if (results.length === 0) {
        container.innerHTML = '<div class="search-empty">No results found.</div>';
        return;
    }
    container.innerHTML = results.map((r, i) =>
        `<a href="${r.url}" class="search-result ${i === 0 ? 'active' : ''}">
            <span class="search-result-title">${r.title}</span>
        </a>`
    ).join('');
}

document.addEventListener('keydown', function(e) {
    if ((e.metaKey || e.ctrlKey) && e.key === 'k') { e.preventDefault(); openSearch(); }
    if (e.key === 'Escape') closeSearch();
    const overlay = document.querySelector('.search-overlay');
    if (!overlay || !overlay.classList.contains('active')) return;
    const results = overlay.querySelectorAll('.search-result');
    const active = overlay.querySelector('.search-result.active');
    if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
        e.preventDefault();
        let idx = Array.from(results).indexOf(active);
        if (e.key === 'ArrowDown') idx = Math.min(idx + 1, results.length - 1);
        else idx = Math.max(idx - 1, 0);
        results.forEach(r => r.classList.remove('active'));
        results[idx].classList.add('active');
        results[idx].scrollIntoView({ block: 'nearest' });
    }
    if (e.key === 'Enter' && active) { window.location.href = active.href; }
});

document.addEventListener('DOMContentLoaded', function() {
    const overlay = document.querySelector('.search-overlay');
    if (overlay) {
        overlay.addEventListener('click', function(e) { if (e.target === overlay) closeSearch(); });
    }
    const input = document.querySelector('.search-input');
    if (input) { input.addEventListener('input', function() { updateSearchResults(this.value); }); }
});

// ── Theme Toggle ────────────────────────────────────────

const lightTheme = {
    '--bg-primary': '#ffffff',
    '--bg-secondary': '#f6f8fa',
    '--bg-tertiary': '#eaeef2',
    '--bg-code': '#f6f8fa',
    '--border': '#d0d7de',
    '--text-primary': '#1f2328',
    '--text-secondary': '#656d76',
    '--text-muted': '#8b949e',
    '--accent': '#0969da',
    '--accent-hover': '#0550ae',
    '--accent-dim': '#0969da22',
};

const darkTheme = {
    '--bg-primary': '#0d1117',
    '--bg-secondary': '#161b22',
    '--bg-tertiary': '#21262d',
    '--bg-code': '#1a1e25',
    '--border': '#30363d',
    '--text-primary': '#e6edf3',
    '--text-secondary': '#8b949e',
    '--text-muted': '#6e7681',
    '--accent': '#58a6ff',
    '--accent-hover': '#79c0ff',
    '--accent-dim': '#1f6feb33',
};

function toggleTheme() {
    const current = localStorage.getItem('theme') || 'dark';
    const next = current === 'dark' ? 'light' : 'dark';
    applyTheme(next);
    localStorage.setItem('theme', next);
}

function applyTheme(theme) {
    const vars = theme === 'light' ? lightTheme : darkTheme;
    const root = document.documentElement;
    for (const [key, value] of Object.entries(vars)) {
        root.style.setProperty(key, value);
    }
    const btn = document.querySelector('.theme-toggle');
    if (btn) btn.textContent = theme === 'light' ? '\u263E' : '\u2600';
}

// Apply saved theme on load
(function() {
    const saved = localStorage.getItem('theme') || 'dark';
    if (saved === 'light') applyTheme('light');
})();

// ── Table of Contents ───────────────────────────────────

document.addEventListener('DOMContentLoaded', function() {
    const toc = document.querySelector('.page-toc');
    if (!toc) return;
    const headings = document.querySelectorAll('.content h2[id], .content section[id] > h2');
    if (headings.length < 2) { toc.style.display = 'none'; return; }

    const tocHTML = ['<div class="toc-title">On this page</div>'];
    headings.forEach(function(h) {
        const id = h.id || (h.closest('section') && h.closest('section').id);
        if (!id) return;
        const text = h.textContent;
        tocHTML.push(`<a href="#${id}" class="toc-link">${text}</a>`);
    });
    toc.innerHTML = tocHTML.join('');

    // Highlight active section on scroll
    const links = toc.querySelectorAll('.toc-link');
    let ticking = false;
    window.addEventListener('scroll', function() {
        if (ticking) return;
        ticking = true;
        requestAnimationFrame(function() {
            let current = '';
            headings.forEach(function(h) {
                const id = h.id || (h.closest('section') && h.closest('section').id);
                if (!id) return;
                const rect = h.getBoundingClientRect();
                if (rect.top <= 100) current = id;
            });
            links.forEach(function(link) {
                link.classList.toggle('active', link.getAttribute('href') === '#' + current);
            });
            ticking = false;
        });
    });
});

// ── Mobile Menu ─────────────────────────────────────────

function toggleMenu() {
    const sidebar = document.querySelector('.sidebar');
    if (sidebar) sidebar.classList.toggle('open');
}

// Close sidebar on outside click (mobile)
document.addEventListener('click', function(e) {
    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.menu-toggle');
    if (sidebar && sidebar.classList.contains('open') &&
        !sidebar.contains(e.target) && !toggle.contains(e.target)) {
        sidebar.classList.remove('open');
    }
});

// ── Active Nav Link ─────────────────────────────────────

(function() {
    const current = window.location.pathname.split('/').pop() || 'index.html';
    document.querySelectorAll('.sidebar nav a').forEach(function(a) {
        if (a.getAttribute('href') === current) {
            a.classList.add('active');
        } else {
            a.classList.remove('active');
        }
    });
})();

// ── Copy Code Blocks ────────────────────────────────────

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('pre').forEach(function(pre) {
        const btn = document.createElement('button');
        btn.className = 'copy-btn';
        btn.textContent = 'Copy';
        btn.addEventListener('click', function() {
            const code = pre.querySelector('code');
            const text = code ? code.textContent : pre.textContent;
            navigator.clipboard.writeText(text).then(function() {
                btn.textContent = 'Copied!';
                setTimeout(function() { btn.textContent = 'Copy'; }, 2000);
            });
        });
        pre.style.position = 'relative';
        pre.appendChild(btn);
    });
});
