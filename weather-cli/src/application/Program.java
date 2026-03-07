package application;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.JSONObject;

public class Program {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.print("Digite o nome da cidade: ");
		String cidade = sc.nextLine();

		try {
			String weatherData = getWeatherData(cidade); // retorna JSON

			// Erro 1006 = localizao nao encontrada
			JSONObject json = new JSONObject(weatherData);

			if (json.has("error")) {
				System.out.println("Localização não encontrada!");
			} else {
				printWeatherData(weatherData);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		sc.close();
	}

	private static String getWeatherData(String cidade) throws Exception {

		String apiKey = Files.readString(Paths.get("api-key.txt")).trim();

		if (apiKey.isEmpty()) {
			throw new Exception("API Key vazia!");
		}

		String cityNameFormat = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
		String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + cityNameFormat;

		// constroi uma solicitação http e define a uri da solicitação
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();

		// cria um objeto para enviar solicitações http e receber respostas http
		HttpClient client = HttpClient.newHttpClient();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body(); // retorna os dados obtidos do site da api

	}

	private static void printWeatherData(String weatherData) {

		JSONObject jsonData = new JSONObject(weatherData);
		JSONObject info = jsonData.getJSONObject("current");

		// dados localizacao
		String cidade = jsonData.getJSONObject("location").getString("name");
		String estado = jsonData.getJSONObject("location").getString("region");
		String pais = jsonData.getJSONObject("location").getString("country");

		// dados climaticos
		String weatherCondition = info.getJSONObject("condition").getString("text");
		int humidity = info.getInt("humidity");
		float windVelocity = info.getFloat("wind_kph");
		float atmosphericPressure = info.getFloat("pressure_mb");
		float feelslike = info.getFloat("feelslike_c");
		float temp = info.getFloat("temp_c");

		// dados data-hora
		String dateTimeString = info.getString("last_updated");

		System.out.println("\n=== Clima Atual ===");
		System.out.println(cidade + " (" + estado + "), " + pais);
		System.out.println("Atualizado em: " + dateTimeString);
		System.out.println("-------------------------");
		System.out.println("Temperatura: " + temp + " °C");
		System.out.println("Sensação Térmica: " + feelslike + " °C");
		System.out.println("Condição: " + weatherCondition);
		System.out.println("Umidade: " + humidity + "%");
		System.out.println("Vento: " + windVelocity + " km/h");
		System.out.println("Pressão: " + atmosphericPressure + " mb");
	}

}
