package com.felipe.screenmatch.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiConsult {
        public static String getTranslation(String texto){
            String model = "gemini-2.0-flash-lite"; // Pode modificar a versão se desejar
            String prompt = "Traduz o seguinte texto para português brasileiro: " + texto;

            Client client = new Client.Builder().apiKey("AIzaSyD_cTaFgYe4Su2XHHCLSnr9lvym5zZ-_No").build();

            try{
                GenerateContentResponse response = client.models.generateContent(model,
                        prompt,
                        null); // parâmetro de configurações adicionais

                if(!response.text().isEmpty()) {
                    return response.text();
                }
            }catch (Exception e){
                System.err.println("Erro ao chamar a API Gemini para tradução: " + e.getMessage());
            }

            return null;
        }
}
