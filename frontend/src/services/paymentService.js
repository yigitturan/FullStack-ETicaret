import api from "./api";

// olusan siparis icin odeme istegi atiyoruz
export const payOrder = async (paymentData) => {
    const response = await api.post("/api/payments/pay", paymentData);
    return response.data;
};