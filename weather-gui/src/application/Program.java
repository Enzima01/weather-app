package application;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.json.JSONObject;

import com.formdev.flatlaf.FlatLightLaf;

public class Program extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtCidade;

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception ex) {
			System.err.println("Failed to initialize LaF");
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Program frame = new Program();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Program() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Program.class.getResource("/imgs/ico.png")));
		setTitle("Weather App - Enzima01");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 489, 524);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtCidade = new JTextField();
		txtCidade.setHorizontalAlignment(SwingConstants.CENTER);
		txtCidade.setFont(new Font("SansSerif", Font.PLAIN, 20));
		txtCidade.setToolTipText("");
		txtCidade.setBounds(75, 48, 322, 45);
		contentPane.add(txtCidade);
		txtCidade.setColumns(10);

		JLabel lblDigitar = new JLabel("Digite o nome da cidade:");
		lblDigitar.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lblDigitar.setBounds(116, 11, 240, 26);
		contentPane.add(lblDigitar);

		JButton btnBuscar = new JButton("Buscar");

		btnBuscar.setFont(new Font("SansSerif", Font.PLAIN, 15));
		btnBuscar.setBounds(177, 105, 119, 23);
		contentPane.add(btnBuscar);

		JLabel fotoClima = new JLabel("");
		fotoClima.setHorizontalAlignment(SwingConstants.CENTER);
		fotoClima.setFont(new Font("SansSerif", Font.PLAIN, 20));
		fotoClima.setBackground(Color.RED);
		fotoClima.setBounds(204, 139, 64, 64);
		contentPane.add(fotoClima);

		JLabel lblCidadeEstado = new JLabel("");
		lblCidadeEstado.setHorizontalAlignment(SwingConstants.CENTER);
		lblCidadeEstado.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lblCidadeEstado.setBounds(10, 332, 453, 39);
		contentPane.add(lblCidadeEstado);

		JLabel lblPais = new JLabel("");
		lblPais.setHorizontalAlignment(SwingConstants.CENTER);
		lblPais.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lblPais.setBounds(15, 382, 443, 26);
		contentPane.add(lblPais);

		JLabel lblTemperatura = new JLabel("");
		lblTemperatura.setHorizontalAlignment(SwingConstants.CENTER);
		lblTemperatura.setFont(new Font("SansSerif", Font.PLAIN, 50));
		lblTemperatura.setBounds(10, 227, 453, 107);
		contentPane.add(lblTemperatura);

		JLabel lblCondicao = new JLabel("");
		lblCondicao.setHorizontalAlignment(SwingConstants.CENTER);
		lblCondicao.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lblCondicao.setBounds(10, 198, 453, 26);
		contentPane.add(lblCondicao);

		JLabel lblDataHora = new JLabel("");
		lblDataHora.setHorizontalAlignment(SwingConstants.CENTER);
		lblDataHora.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lblDataHora.setBounds(15, 448, 443, 29);
		contentPane.add(lblDataHora);

		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String cidade = txtCidade.getText().trim();
				
				if (cidade.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Digite uma cidade!", "Aviso", JOptionPane.WARNING_MESSAGE);
					return;
				}

				try {
					btnBuscar.setEnabled(false);
				
					String weatherData = getWeatherData(cidade);
					
					if (!weatherData.trim().startsWith("{")) {
						JOptionPane.showMessageDialog(null,"Não foi possível obter os dados do clima. Tente novamente!", "Erro", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					JSONObject jsonData = new JSONObject(weatherData);

					if (jsonData.has("error")) {
						JOptionPane.showMessageDialog(null, "Localização não encontrada!", "Error", JOptionPane.ERROR_MESSAGE);
					} else {
						
						JSONObject info = jsonData.getJSONObject("current");
						JSONObject condition = info.getJSONObject("condition");

						// foto
						String foto = condition.getString("icon");
						foto = "https:" + foto;
						ImageIcon icon = new ImageIcon(new URL(foto));
						Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
						fotoClima.setIcon(new ImageIcon(img));

						// cidade e estado
						cidade = jsonData.getJSONObject("location").getString("name");
						String estado = jsonData.getJSONObject("location").getString("region");
						lblCidadeEstado.setText(cidade + " - " + estado);

						// pais
						String pais = jsonData.getJSONObject("location").getString("country");
						lblPais.setText(pais);

						// tempetarura
						float temperatura = info.getFloat("temp_c");
						lblTemperatura.setText(Float.toString(temperatura) + "°C");

						// condição
						String weatherCondition = condition.getString("text");
						lblCondicao.setText(weatherCondition);

						// data-hora
						String dateTimeString = info.getString("last_updated");
						lblDataHora.setText(dateTimeString);
					}
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}finally {
					btnBuscar.setEnabled(true);
				}
			}
		});

	}

	private static String getWeatherData(String cidade) throws Exception {

		InputStream is = Program.class.getResourceAsStream("/api-key.txt");

		if (is == null) {
		    throw new Exception("Arquivo api-key.txt não encontrado!");
		}

		String apiKey = new String(is.readAllBytes()).trim();
		is.close();

		String cityNameFormat = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
		String apiUrl = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + cityNameFormat;

		// constroi uma solicitação http e define a uri da solicitação
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).timeout(java.time.Duration.ofSeconds(5)).build();

		// cria um objeto para enviar solicitações http e receber respostas http
		HttpClient client = HttpClient.newHttpClient();

		for (int i = 0; i < 3; i++) {
		    try {
		        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		        return response.body();

		    } catch (Exception e) {
		        if (i == 2) throw e;
		        Thread.sleep(1000); // espera 1 segundo
		    }
		}

		throw new Exception("Falha ao conectar com a API");
	}

}
