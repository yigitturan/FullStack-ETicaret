import { useEffect, useState } from "react";
import { login } from "./services/authService";
import { getProducts } from "./services/productService";
import { addToCart, getCart, removeFromCart } from "./services/cartService";
import { checkout } from "./services/orderService";
import { payOrder } from "./services/paymentService";

function App() {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [loading, setLoading] = useState(true);

  const [order, setOrder] = useState(null);

  const [payment, setPayment] = useState(null);

  // sayfa ilk acildiginda login oluyoruz, urunleri ve sepeti getiriyoruz
  useEffect(() => {
    login({
      email: "admin@test.com",
      password: "123456"
    })
      .then((res) => {
        // backend'den gelen JWT tokeni sakliyoruz
        localStorage.setItem("token", res.token);

        // token kaydolduktan sonra urunleri getiriyoruz
        return getProducts(0, 10);
      })
      .then((data) => {
        setProducts(data.content);
        setTotalPages(data.totalPages);
        setLoading(false);

        // urunler geldikten sonra sepeti de getiriyoruz
        loadCart();
      })
      .catch((err) => {
        console.error("HATA:", err);
        setLoading(false);
      });
  }, []);

  // urunleri sayfali sekilde getirir
  const loadPage = (pageNumber) => {
    setLoading(true);

    getProducts(pageNumber, 10)
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

  // sepeti backend'den getirir
  const loadCart = () => {
    // simdilik cartId = 1 kullaniyoruz
    getCart(1)
      .then((data) => {
        setCart(data);
      })
      .catch((err) => {
        console.error("Cart hatasi:", err);
      });
  };

  // urunu sepete ekler
  const handleAddToCart = (productId) => {
    // simdilik her tiklamada 1 adet ekliyoruz
    addToCart(productId, 1)
      .then(() => {
        alert("Urun sepete eklendi");

        // urun eklendikten sonra sepeti yeniliyoruz
        loadCart();
      })
      .catch((err) => {
        console.error("Sepete ekleme hatasi:", err);
        alert("Urun sepete eklenemedi");
      });
  };

  // sepetten urun siler
  const handleRemoveFromCart = (cartItemId) => {
    removeFromCart(cartItemId)
      .then(() => {
        alert("Urun sepetten silindi");

        // silme sonrasi sepeti yeniliyoruz
        loadCart();
      })
      .catch((err) => {
        console.error("Sepetten silme hatasi:", err);
        alert("Urun sepetten silinemedi");
      });
  };

  // sepetten siparis olusturur
  const handleCheckout = () => {
    checkout(1)
      .then((data) => {
        setOrder(data);
        alert("Siparis olusturuldu");

        // siparis olustuktan sonra sepeti tekrar yeniliyoruz
        loadCart();
      })
      .catch((err) => {
        console.error("Checkout hatasi:", err);
        alert("Siparis olusturulamadi");
      });
  };

  // olusan siparis icin odeme yapar
  const handlePayment = () => {
    if (!order) {
      alert("Once siparis olusturmalisin");
      return;
    }

    payOrder({
      orderId: order.orderId,
      cardHolderName: "John Doe",
      cardNumber: "5528790000000008",
      expireMonth: "12",
      expireYear: "2030",
      cvc: "123"
    })
      .then((data) => {
        setPayment(data);
        alert("Odeme islemi tamamlandi");
      })
      .catch((err) => {
        console.error("Odeme hatasi:", err);
        alert("Odeme basarisiz");
      });
  };

  if (loading) {
    return <h2>Yukleniyor...</h2>;
  }

  return (
    <div style={{ padding: "30px" }}>
      <h1>Urunler</h1>

      {products.map((product) => (
        <div
          key={product.id}
          style={{
            border: "1px solid #ccc",
            padding: "15px",
            marginBottom: "10px",
            borderRadius: "8px"
          }}
        >
          <h3>{product.name}</h3>
          <p>{product.description}</p>
          <p><b>Fiyat:</b> {product.price} TL</p>
          <p><b>Stok:</b> {product.stockQuantity}</p>
          <p><b>Kategori:</b> {product.category}</p>

          <button onClick={() => handleAddToCart(product.id)}>
            Sepete Ekle
          </button>
        </div>
      ))}

      <div style={{ marginTop: "20px" }}>
        <button disabled={page === 0} onClick={() => loadPage(page - 1)}>
          Onceki
        </button>

        <span style={{ margin: "0 15px" }}>
          Sayfa {page + 1} / {totalPages}
        </span>

        <button
          disabled={page + 1 >= totalPages}
          onClick={() => loadPage(page + 1)}
        >
          Sonraki
        </button>
      </div>

      <hr style={{ margin: "30px 0" }} />

      <h2>Sepet</h2>

      {!cart && <p>Sepet yukleniyor...</p>}

      {cart && cart.items.length === 0 && <p>Sepet bos</p>}

      {cart && cart.items.map((item) => (
        <div
          key={item.cartItemId}
          style={{
            border: "1px solid #999",
            padding: "10px",
            marginBottom: "10px",
            borderRadius: "8px"
          }}
        >
          <p><b>Urun:</b> {item.productName}</p>
          <p><b>Adet:</b> {item.quantity}</p>
          <p><b>Fiyat:</b> {item.price} TL</p>

          <button onClick={() => handleRemoveFromCart(item.cartItemId)}>
            Sepetten Sil
          </button>
        </div>
      ))}

      {cart && cart.items.length > 0 && (
        <button onClick={handleCheckout}>
          Siparis Olustur
        </button>
      )}

      {order && (
        <div
          style={{
            border: "1px solid green",
            padding: "15px",
            marginTop: "20px",
            borderRadius: "8px"
          }}
        >
          <h2>Olusan Siparis</h2>
          <p><b>Order ID:</b> {order.orderId}</p>
          <p><b>Status:</b> {order.status}</p>
          <p><b>Total:</b> {order.totalAmount} TL</p>
          <p><b>User:</b> {order.userEmail}</p>

          <button onClick={handlePayment}>
            Odeme Yap
          </button>

        </div>

      )}

      {payment && (
        <div
          style={{
            border: "1px solid blue",
            padding: "15px",
            marginTop: "20px",
            borderRadius: "8px"
          }}
        >
          <h2>Odeme Sonucu</h2>
          <p><b>Payment ID:</b> {payment.paymentId}</p>
          <p><b>Order ID:</b> {payment.orderId}</p>
          <p><b>Status:</b> {payment.status}</p>
          <p><b>Message:</b> {payment.message}</p>
          <p><b>Amount:</b> {payment.amount} TL</p>
        </div>
      )}


    </div>
  );
}

export default App;