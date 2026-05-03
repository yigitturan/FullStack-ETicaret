import { useEffect, useState } from "react";
import "./App.css";

import Navbar from "./components/Navbar";
import AuthModal from "./components/AuthModal";
import HeroSlider from "./components/HeroSlider";
import CategoryFilter from "./components/CategoryFilter";
import ProductList from "./components/ProductList";
import CartPanel from "./components/CartPanel";
import Footer from "./components/Footer";
import ProductDetail from "./components/ProductDetail";

import { login, register } from "./services/authService";
import { getProducts } from "./services/productService";
import { addToCart, getMyCart, removeFromCart } from "./services/cartService";
import { checkout } from "./services/orderService";
import { payOrder } from "./services/paymentService";

import { checkoutMyCart } from "./services/orderService";

function App() {
  // sayfa gecislerini burada tutuyorum
  // router kullanmadigim icin simdilik activePage ile yonetiyorum
  const [activePage, setActivePage] = useState("home");

  // urun, sepet, siparis ve odeme bilgileri
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);
  const [order, setOrder] = useState(null);
  const [payment, setPayment] = useState(null);

  // detay sayfasinda gosterecegim urunu burada tutuyorum
  const [selectedProduct, setSelectedProduct] = useState(null);

  // kullanici girisi ve modal bilgileri
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [authMode, setAuthMode] = useState("login");

  // kategori ve arama bilgileri
  const [selectedCategory, setSelectedCategory] = useState("Tumu");
  const [searchText, setSearchText] = useState("");

  // pagination bilgileri
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");

    // token varsa kullaniciyi giris yapmis kabul ediyorum
    if (token) {
      setIsLoggedIn(true);
      loadCart();
    }

    // uygulama ilk acildiginda urunleri getiriyorum
    loadProducts(0);
  }, []);

  const loadProducts = (pageNumber = 0) => {
    setLoading(true);

    getProducts(pageNumber, 8)
      .then((data) => {
        setProducts(data.content);
        setTotalPages(data.totalPages);
        setPage(pageNumber);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Urun listeleme hatasi:", err);
        setLoading(false);
      });
  };

  const loadCart = () => {
      // artik sabit cartId kullanmiyorum, backend token'dan kullaniciyi buluyor
    getMyCart()
      .then((data) => setCart(data))
      .catch((err) => console.error("Cart hatasi:", err));
  };

  const handleAuth = (formData) => {
    // modal hangi moddaysa ona gore login/register istegi atiyorum
    const request =
      authMode === "login"
        ? login({ email: formData.email, password: formData.password })
        : register({
            fullName: formData.fullName,
            email: formData.email,
            password: formData.password
          });

    request
      .then((res) => {
        localStorage.setItem("token", res.token);
        setIsLoggedIn(true);
        setAuthModalOpen(false);
        loadCart();
      })
      .catch((err) => {
        console.error("Auth hatasi:", err.response?.data || err);
        alert("Giris / kayit islemi basarisiz");
      });
  };

  const handleLogout = () => {
    // cikis yapinca kullaniciya ait bilgileri temizliyorum
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    setCart(null);
    setOrder(null);
    setPayment(null);
    setSelectedProduct(null);
    setActivePage("home");
  };

  const handleAddToCart = (productId) => {
    // giris yapmadan sepete ekleme yapilmasin diye kontrol ediyorum
    if (!isLoggedIn) {
      setAuthMode("login");
      setAuthModalOpen(true);
      alert("Sepete urun eklemek icin once giris yapmalisin");
      return;
    }

    addToCart(productId, 1)
      .then(() => {
        loadCart();
        setActivePage("cart");
        alert("Urun sepete eklendi");
      })
      .catch((err) => {
        console.error("Sepete ekleme hatasi:", err);
        alert("Urun sepete eklenemedi");
      });
  };

  const handleRemoveFromCart = (cartItemId) => {
    removeFromCart(cartItemId)
      .then(() => loadCart())
      .catch((err) => console.error("Sepetten silme hatasi:", err));
  };

  const handleCheckout = () => {
    // simdilik sabit cartId ile siparis olusturuyorum
    checkoutMyCart()
      .then((data) => {
        setOrder(data);
        setPayment(null);
        setActivePage("orders");
        alert("Siparis olusturuldu");
      })
      .catch((err) => {
        console.error("Checkout hatasi:", err);
        alert("Siparis olusturulamadi");
      });
  };

  const handlePayment = (card) => {
    if (!order) {
      alert("Once siparis olusturmalisin");
      return;
    }

    // kart bilgilerini backend'in bekledigi formata ceviriyorum
    payOrder({
      orderId: order.orderId,
      cardHolderName: card.cardHolderName.trim(),
      cardNumber: card.cardNumber.replaceAll(" ", ""),
      expireMonth: card.expireMonth.trim(),
      expireYear: card.expireYear.trim(),
      cvc: card.cvc.trim()
    })
      .then((data) => {
        setPayment(data);
        alert("Odeme islemi tamamlandi");
      })
      .catch((err) => {
        console.error("Odeme hatasi:", err.response?.data || err);
        alert("Odeme basarisiz");
      });
  };

  // kategorileri backend'den gelen urunlere gore olusturuyorum
  const categories = [
    "Tumu",
    ...new Set(products.map((product) => product.category).filter(Boolean))
  ];

  // secilen kategoriye ve arama metnine gore urunleri filtreliyorum
  const filteredProducts = products.filter((product) => {
    const categoryMatch =
      selectedCategory === "Tumu" || product.category === selectedCategory;

    const searchMatch =
      product.name?.toLowerCase().includes(searchText.toLowerCase()) ||
      product.description?.toLowerCase().includes(searchText.toLowerCase()) ||
      product.category?.toLowerCase().includes(searchText.toLowerCase());

    return categoryMatch && searchMatch;
  });

  return (
    <div>
      <Navbar
        isLoggedIn={isLoggedIn}
        searchText={searchText}
        setSearchText={setSearchText}
        activePage={activePage}
        setActivePage={setActivePage}
        onLoginClick={() => {
          setAuthMode("login");
          setAuthModalOpen(true);
        }}
        onRegisterClick={() => {
          setAuthMode("register");
          setAuthModalOpen(true);
        }}
        onLogout={handleLogout}
      />

      {activePage === "home" && (
        <>
          <HeroSlider />

          <main className="main-layout">
            <section className="content-area">
              <CategoryFilter
                categories={categories}
                selectedCategory={selectedCategory}
                setSelectedCategory={setSelectedCategory}
              />

              <ProductList
                products={filteredProducts}
                loading={loading}
                onAddToCart={handleAddToCart}
                onProductClick={(product) => {
                  setSelectedProduct(product);
                  setActivePage("productDetail");
                }}
              />

              <div className="pagination">
                <button disabled={page === 0} onClick={() => loadProducts(page - 1)}>
                  Onceki
                </button>

                <span>
                  Sayfa {page + 1} / {totalPages || 1}
                </span>

                <button
                  disabled={page + 1 >= totalPages}
                  onClick={() => loadProducts(page + 1)}
                >
                  Sonraki
                </button>
              </div>
            </section>
          </main>
        </>
      )}

      {activePage === "productDetail" && (
        <ProductDetail
          product={selectedProduct}
          onBack={() => setActivePage("home")}
          onAddToCart={handleAddToCart}
        />
      )}

      {activePage === "cart" && (
        <main className="cart-page">
          <section className="cart-page-card">
            <h1>Sepetim</h1>

            <CartPanel
              isLoggedIn={isLoggedIn}
              cart={cart}
              order={order}
              payment={payment}
              onRemoveFromCart={handleRemoveFromCart}
              onCheckout={handleCheckout}
              onPayment={handlePayment}
              onLoginRequired={() => {
                setAuthMode("login");
                setAuthModalOpen(true);
              }}
            />
          </section>
        </main>
      )}

      {activePage === "orders" && (
        <main className="orders-page">
          <section className="orders-card">
            <h1>Siparislerim</h1>

            {!order && (
              <div className="empty-order">
                <h2>Henuz siparis olusturmadin</h2>
                <p>Urunleri inceleyip sepetine ekleyerek siparis olusturabilirsin.</p>
                <button onClick={() => setActivePage("home")}>
                  Alisverise Don
                </button>
              </div>
            )}

            {order && (
              <div className="order-detail">
                <h2>Siparis Detayi</h2>

                <div className="order-summary-grid">
                  <div>
                    <span>Order ID</span>
                    <b>{order.orderId}</b>
                  </div>

                  <div>
                    <span>Durum</span>
                    <b>{payment ? "PAID" : order.status}</b>
                  </div>

                  <div>
                    <span>Toplam Tutar</span>
                    <b>{order.totalAmount} TL</b>
                  </div>

                  <div>
                    <span>Kullanici</span>
                    <b>{order.userEmail}</b>
                  </div>
                </div>

                <h3>Urunler</h3>

                {order.items &&
                  order.items.map((item, index) => (
                    <div className="order-item" key={index}>
                      <div>
                        <b>{item.productName}</b>
                        <p>Adet: {item.quantity}</p>
                      </div>

                      <b>{item.totalPrice} TL</b>
                    </div>
                  ))}

                {!payment && (
                  <CartPanel
                    isLoggedIn={isLoggedIn}
                    cart={{ items: [] }}
                    order={order}
                    payment={payment}
                    onRemoveFromCart={handleRemoveFromCart}
                    onCheckout={handleCheckout}
                    onPayment={handlePayment}
                    onLoginRequired={() => {
                      setAuthMode("login");
                      setAuthModalOpen(true);
                    }}
                  />
                )}

                {payment && (
                  <div className="payment-result large">
                    <h3>Odeme Tamamlandi</h3>
                    <p>
                      <b>Status:</b> {payment.status}
                    </p>
                    <p>{payment.message}</p>
                    <p>
                      <b>Tutar:</b> {payment.amount} TL
                    </p>
                  </div>
                )}
              </div>
            )}
          </section>
        </main>
      )}

      <Footer />

      {authModalOpen && (
        <AuthModal
          mode={authMode}
          setMode={setAuthMode}
          onClose={() => setAuthModalOpen(false)}
          onSubmit={handleAuth}
        />
      )}
    </div>
  );
}

export default App;