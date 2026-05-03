import api from "./api";

// olusan order icin iyzico odeme istegi atiyoruz
export const payOrder = async (paymentData) => {
    const response = await api.post("/api/payments/pay", paymentData);
    return response.data;
};