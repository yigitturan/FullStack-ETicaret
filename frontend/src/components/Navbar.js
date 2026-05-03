function Navbar({
  isLoggedIn,
  searchText,
  setSearchText,
  activePage,
  setActivePage,
  onLoginClick,
  onRegisterClick,
  onLogout
}) {
  return (
    <header className="navbar">
      <div className="brand" onClick={() => setActivePage("home")}>
        y<span>11</span>
      </div>

      <input
        className="search-input"
        placeholder="Urun, kategori veya marka ara"
        value={searchText}
        onChange={(e) => setSearchText(e.target.value)}
      />

      <div className="nav-actions">
       {isLoggedIn && (
         <>
           <button
             className={activePage === "home" ? "nav-btn active-nav" : "nav-btn"}
             onClick={() => setActivePage("home")}
           >
              🏠
           </button>

           <button
             className={activePage === "orders" ? "nav-btn active-nav" : "nav-btn"}
             onClick={() => setActivePage("orders")}
           >
             📦 Siparislerim
           </button>
         </>
       )}

        {!isLoggedIn ? (
          <>
            <button className="outline-btn" onClick={onLoginClick}>
              Giris Yap
            </button>

            <button onClick={onRegisterClick}>
              Kayit Ol
            </button>
          </>
        ) : (
          <button className="outline-btn" onClick={onLogout}>
            Cikis Yap
          </button>
        )}
      </div>
    </header>
  );
}

export default Navbar;