import { useState } from "react";

function AuthModal({ mode, setMode, onClose, onSubmit }) {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: ""
  });

  const handleSubmit = (e) => {
    e.preventDefault();

    // form bilgilerini App.js'e gonderiyoruz
    onSubmit(formData);
  };

  return (
    <div className="modal-backdrop">
      <div className="auth-modal">
        <button className="close-btn" onClick={onClose}>
          X
        </button>

        <div className="brand modal-brand">
          y<span>11</span>
        </div>

        <h2>{mode === "login" ? "Giris Yap" : "Kayit Ol"}</h2>

        <form onSubmit={handleSubmit}>
          {mode === "register" && (
            <input
              placeholder="Ad Soyad"
              value={formData.fullName}
              onChange={(e) =>
                setFormData({ ...formData, fullName: e.target.value })
              }
            />
          )}

          <input
            placeholder="Email"
            value={formData.email}
            onChange={(e) =>
              setFormData({ ...formData, email: e.target.value })
            }
          />

          <input
            type="password"
            placeholder="Sifre"
            value={formData.password}
            onChange={(e) =>
              setFormData({ ...formData, password: e.target.value })
            }
          />

          <button className="full-btn" type="submit">
            {mode === "login" ? "Giris Yap" : "Kayit Ol"}
          </button>
        </form>

        <p className="switch-text">
          {mode === "login" ? "Hesabin yok mu?" : "Zaten hesabin var mi?"}
          <button
            onClick={() => setMode(mode === "login" ? "register" : "login")}
          >
            {mode === "login" ? "Kayit Ol" : "Giris Yap"}
          </button>
        </p>
      </div>
    </div>
  );
}

export default AuthModal;