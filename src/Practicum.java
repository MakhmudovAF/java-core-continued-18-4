import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class Practicum {

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();

        // IP, информацию о котором хотим получить
        String ip = "213.186.33.69";

        // URL: ipwho.is + IP + язык локализации fr
        URI url = URI.create("https://ipwho.is/" + ip + "?lang=fr");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                if (!jsonElement.isJsonObject()) {
                    System.out.println("Ответ от сервера не соответствует ожидаемому.");
                    return;
                }

                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // Иногда API возвращает success=false (например, если IP некорректный)
                if(!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                    System.out.println("Ответ от сервера не соответствует ожидаемому.");
                    return;
                }

                String country = jsonObject.get("country").getAsString();
                String city = jsonObject.get("city").getAsString();
                double latitude = jsonObject.get("latitude").getAsDouble();

                // поля из задания
                double longitude = jsonObject.get("longitude").getAsDouble();
                String countryNeighbours = jsonObject.get("country_neighbours").getAsString();

                // country_phone обычно приходит строкой вида "+33"
                String countryPhoneRaw = jsonObject.get("country_phone").getAsString();
                int countryPhone = Integer.parseInt(countryPhoneRaw.replace("+", "").trim());

                System.out.println("Страна: " + country);
                System.out.println("Город: " + city);
                System.out.println("Широта: " + latitude);
                System.out.println("Долгота: " + longitude);
                System.out.println("Соседние страны: " + countryNeighbours);
                System.out.println("Телефонный код страны: " + countryPhone);

            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}