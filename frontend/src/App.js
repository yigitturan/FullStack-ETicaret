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
import ProductAssistant from "./components/ProductAssistant";

import { login, register } from "./services/authService";
import { getProducts } from "./services/productService";
import { addToCart, getMyCart, removeFromCart } from "./services/cartService";
import { checkoutMyCart } from "./services/orderService";
import { payOrder } from "./services/paymentService";

function App() {

  // sayfa kontrolu
  const [activePage, setActivePage] = useState("home");

  // data state'leri
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);
  const [order, setOrder] = useState(null);
  const [payment, setPayment] = useState(null);

  const [selectedProduct, setSelectedProduct] = useState(null);

  // auth state
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [authMode, setAuthMode] = useState("login");

  // filtreler
  const [selectedCategory, setSelectedCategory] = useState("Tumu");
  const [searchText, setSearchText] = useState("");

  // pagination
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token) {
      setIsLoggedIn(true);
      loadCart();
    }

    loadProducts(0);
  }, []);

  const loadProducts = (pageNumber = 0) => {
    setLoading(true);

    getProducts(pageNumber, 8)
      .then((data) => {
        setProducts(data.content);
        setTotalPages(data.totalPages);
        setPage(pageNumber);
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  };

  const loadCart = () => {
    getMyCart()
      .then(setCart)
      .catch(console.error);
  };

  const handleAuth = (formData) => {
    const request =
      authMode === "login"
        ? login({ email: formData.email, password: formData.password })
        : register(formData);

    request
      .then((res) => {
        localStorage.setItem("token", res.token);
        setIsLoggedIn(true);
        setAuthModalOpen(false);
        loadCart();
      })
      .catch(() => alert("Auth hatasi"));
  };

  const handleAddToCart = (productId) => {
    if (!isLoggedIn) {
      setAuthMode("login");
      setAuthModalOpen(true);
      return;
    }

    addToCart(productId, 1)
      .then(() => {
        loadCart();
        setActivePage("cart");
      })
      .catch(console.error);
  };

  const handleCheckout = () => {
    checkoutMyCart()
      .then((data) => {
        setOrder(data);
        setActivePage("orders");
      })
      .catch(console.error);
  };

  const handlePayment = (card) => {
    payOrder({
      orderId: order.orderId,
      ...card
    })
      .then(setPayment)
      .catch(console.error);
  };

  const categories = [
    "Tumu",
    ...new Set(products.map((p) => p.category).filter(Boolean))
  ];

  const filteredProducts = products.filter((p) => {
    return (
      (selectedCategory === "Tumu" || p.category === selectedCategory) &&
      (p.name?.toLowerCase().includes(searchText.toLowerCase()) ||
        p.category?.toLowerCase().includes(searchText.toLowerCase()))
    );
  });

  return (
    <>
      {/* Ana layout */}
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
        />

        {activePage === "home" && (
          <>
            <HeroSlider />

            <CategoryFilter
              categories={categories}
              selectedCategory={selectedCategory}
              setSelectedCategory={setSelectedCategory}
            />

            <ProductList
              products={filteredProducts}
              loading={loading}
              onAddToCart={handleAddToCart}
              onProductClick={(p) => {
                setSelectedProduct(p);
                setActivePage("productDetail");
              }}
            />
          </>
        )}

        {activePage === "productDetail" && (
          <ProductDetail
            product={selectedProduct}
            onBack={() => setActivePage("home")}
            onAddToCart={handleAddToCart}
          />
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

      {/*BURASI KRITIK FIX */}
      {/* Fragment kullandik, artik JSX hatasi yok */}
      <ProductAssistant />
    </>
  );
}

export default App;