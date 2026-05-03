import { useState } from "react";

function PaymentForm({ onPayment }) {
  const [card, setCard] = useState({
    cardHolderName: "",
    cardNumber: "",
    expireMonth: "",
    expireYear: "",
    cvc: ""
  });

  const handleSubmit = (e) => {
    e.preventDefault();

    // kart bilgilerini App.js'e gonderiyoruz
    onPayment(card);
  };

  return (
    <form className="payment-form" onSubmit={handleSubmit}>
      <h3>Kart Bilgileri</h3>

      <input
        placeholder="Kart Sahibi"
        value={card.cardHolderName}
        onChange={(e) => setCard({ ...card, cardHolderName: e.target.value })}
      />

      <input
        placeholder="Kart Numarasi"
        value={card.cardNumber}
        onChange={(e) => setCard({ ...card, cardNumber: e.target.value })}
      />

      <div className="card-row">
        <input
          placeholder="Ay"
          value={card.expireMonth}
          onChange={(e) => setCard({ ...card, expireMonth: e.target.value })}
        />

        <input
          placeholder="Yil"
          value={card.expireYear}
          onChange={(e) => setCard({ ...card, expireYear: e.target.value })}
        />

        <input
          placeholder="CVC"
          value={card.cvc}
          onChange={(e) => setCard({ ...card, cvc: e.target.value })}
        />
      </div>

      <button className="pay-btn" type="submit">
        Odeme Yap
      </button>

      <small>Test karti: 5528790000000008 / 12 / 2030 / 123</small>
    </form>
  );
}

export default PaymentForm;