import { useState } from "react";
import { askAssistant } from "../services/assistantService";
import "./ProductAssistant.css";

function ProductAssistant() {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([
    {
      sender: "assistant",
      text: "Merhaba, ben y11 urun asistanin. Sana urun onerebilirim.",
    },
  ]);
  const [loading, setLoading] = useState(false);

  const handleSend = async () => {
    if (!message.trim()) return;

    const userMessage = {
      sender: "user",
      text: message,
    };

    setMessages((prev) => [...prev, userMessage]);
    setMessage("");
    setLoading(true);

    try {
      const data = await askAssistant(message);

      setMessages((prev) => [
        ...prev,
        {
          sender: "assistant",
          text: data.answer,
        },
      ]);
    } catch (error) {
      setMessages((prev) => [
        ...prev,
        {
          sender: "assistant",
          text: "Su an asistana ulasilamiyor. Lutfen tekrar dene.",
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <button className="assistant-button" onClick={() => setOpen(!open)}>
        y11 🤖 Akilli Asistan
      </button>

      {open && (
        <div className="assistant-panel">
          <div className="assistant-header">
            <span>y11 Asistan</span>
            <button onClick={() => setOpen(false)}>x</button>
          </div>

          <div className="assistant-messages">
            {messages.map((item, index) => (
              <div
                key={index}
                className={
                  item.sender === "user"
                    ? "assistant-message user"
                    : "assistant-message bot"
                }
              >
                {item.text}
              </div>
            ))}

            {loading && (
              <div className="assistant-message bot">
                Asistan cevap hazirliyor...
              </div>
            )}
          </div>

          <div className="assistant-input-area">
            <input
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Urunlerle ilgili soru sor..."
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  handleSend();
                }
              }}
            />
            <button onClick={handleSend}>Gonder</button>
          </div>
        </div>
      )}
    </>
  );
}

export default ProductAssistant;