import PaymentForm from "./PaymentForm";

function CartPanel({
  isLoggedIn,
  cart,
  order,
  payment,
  onRemoveFromCart,
  onCheckout,
  onPayment,
  onLoginRequired
}) {
  if (!isLoggedIn) {
    return (
      <aside className="cart-panel">
        <h2>Sepetim</h2>
        <p>Sepeti kullanmak icin giris yapmalisin.</p>
        <button className="full-btn" onClick={onLoginRequired}>
          Giris Yap
        </button>
      </aside>
    );
  }

  return (
    <aside className="cart-panel">
      <h2>Sepetim</h2>

      {!cart && <p>Sepet yukleniyor...</p>}

      {cart && cart.items.length === 0 && (
        <p>Sepetin bos. Urun ekleyerek baslayabilirsin.</p>
      )}

      {cart && cart.items.map((item) => (
        <div className="cart-item" key={item.cartItemId}>
          <div>
            <b>{item.productName}</b>
            <p>Adet: {item.quantity}</p>
            <p>{item.price} TL</p>
          </div>

          <button className="danger-btn" onClick={() => onRemoveFromCart(item.cartItemId)}>
            Sil
          </button>
        </div>
      ))}

      {cart && cart.items.length > 0 && (
        <button className="checkout-btn" onClick={onCheckout}>
          Siparis Olustur
        </button>
      )}

      {order && (
        <div className="order-box">
          <h3>Siparis Bilgisi</h3>
          <p><b>Order ID:</b> {order.orderId}</p>
          <p><b>Status:</b> {order.status}</p>
          <p><b>Total:</b> {order.totalAmount} TL</p>
        </div>
      )}

      {order && <PaymentForm onPayment={onPayment} />}

      {payment && (
        <div className="payment-result">
          <h3>Odeme Sonucu</h3>
          <p><b>Status:</b> {payment.status}</p>
          <p>{payment.message}</p>
          <p><b>Tutar:</b> {payment.amount} TL</p>
        </div>
      )}
    </aside>
  );
}

export default CartPanel;