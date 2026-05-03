import api from "./api";

export const askAssistant = async (message) => {
  const response = await api.post("/api/assistant/ask", {
    message: message,
  });

  return response.data;
};