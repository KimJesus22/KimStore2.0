package com.kimstore.pc_backend;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarReciboCompra(String destinoEmail, Pedido pedido) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinoEmail);
            helper.setSubject("Recibo de Compra - Pedido #" + pedido.getId() + " | KimStore 2.0");

            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e5e7eb; border-radius: 16px; overflow: hidden;'>");

            htmlBuilder.append("<div style='background-color: #2563eb; padding: 24px; text-align: center; color: white;'>");
            htmlBuilder.append("<h1 style='margin: 0; font-size: 24px;'>Gracias por tu compra</h1>");
            htmlBuilder.append("<p style='margin: 4px 0 0 0; opacity: 0.9;'>Tu orden ha sido procesada con exito</p>");
            htmlBuilder.append("</div>");

            htmlBuilder.append("<div style='padding: 24px; background-color: #ffffff;'>");
            htmlBuilder.append("<p style='font-size: 14px; color: #9ca3af; margin: 0; text-transform: uppercase;'>Pedido #")
                    .append(pedido.getId())
                    .append("</p>");
            htmlBuilder.append("<h2 style='font-size: 18px; color: #111827; margin: 12px 0 16px 0; border-bottom: 2px solid #f3f4f6; padding-bottom: 8px;'>Resumen del Carrito</h2>");

            htmlBuilder.append("<table style='width: 100%; border-collapse: collapse; text-align: left;'>");
            for (ItemPedido item : pedido.getItems()) {
                htmlBuilder.append("<tr>")
                        .append("<td style='padding: 8px 0; color: #374151; font-weight: bold;'>")
                        .append(item.getNombreProducto())
                        .append(" <span style='color: #6b7280; font-weight: normal;'>x")
                        .append(item.getCantidad())
                        .append("</span></td>")
                        .append("<td style='padding: 8px 0; text-align: right; color: #111827; font-weight: bold;'>$")
                        .append(String.format("%,.2f", item.getPrecioUnitario() * item.getCantidad()))
                        .append("</td>")
                        .append("</tr>");
            }
            htmlBuilder.append("</table>");

            htmlBuilder.append("<div style='margin-top: 20px; padding-top: 16px; border-top: 2px solid #f3f4f6;'>");
            htmlBuilder.append("<p style='font-size: 18px; font-weight: 900; color: #111827; margin: 0; text-align: right;'>Total Pagado: <span style='color: #10b981;'>$")
                    .append(String.format("%,.2f", pedido.getTotal()))
                    .append("</span></p>");
            htmlBuilder.append("</div>");

            htmlBuilder.append("</div>");

            htmlBuilder.append("<div style='background-color: #f9fafb; padding: 16px; text-align: center; font-size: 12px; color: #9ca3af; border-top: 1px solid #e5e7eb;'>");
            htmlBuilder.append("<p style='margin: 0;'>KimStore 2.0 - Gestion de Inventario & E-commerce</p>");
            htmlBuilder.append("</div>");

            htmlBuilder.append("</div>");

            helper.setText(htmlBuilder.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error critico al enviar el correo: " + e.getMessage());
        }
    }
}
