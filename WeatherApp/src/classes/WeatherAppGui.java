package classes;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

public class WeatherAppGui extends JFrame implements ActionListener{
	
	public JSONObject weatherData;
	
	JFrame frame;
	JTextField searchTextField;
	JLabel weatherConditionImage, temperatureText, weatherConditionsDesc, humidityText, windspeedText;
	JButton searchButton;
	
	public WeatherAppGui() {
		frame = new JFrame("Weather App");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(450, 650);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		addGuiComponents();
	}
	
	public void addGuiComponents() {
		
		searchTextField = new JTextField();
		searchTextField.setBounds(15, 15, 351, 45);
		searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
		frame.add(searchTextField);
				
		weatherConditionImage = new JLabel(loadImage("src/weatherapp_images/cloudy.png"));
		weatherConditionImage.setBounds(0, 125, 450, 217);
		frame.add(weatherConditionImage);
		
		temperatureText = new JLabel("10 C");
		temperatureText.setBounds(0, 350, 450, 54);
		temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
		temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
		frame.add(temperatureText);
		
		weatherConditionsDesc = new JLabel("Cloudy");
		weatherConditionsDesc.setBounds(0, 405, 450, 36);
		weatherConditionsDesc.setFont(new Font("Dialog", Font.PLAIN, 36));
		weatherConditionsDesc.setHorizontalAlignment(SwingConstants.CENTER);
		frame.add(weatherConditionsDesc);
		
		JLabel humidityImage = new JLabel(loadImage("src/weatherapp_images/humidity.png"));
		humidityImage.setBounds(15, 500, 74, 66);
		frame.add(humidityImage);
		
		humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
		humidityText.setBounds(90, 500, 85, 55);
		humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
		frame.add(humidityText);
		
		JLabel windspeedImage = new JLabel(loadImage("src/weatherapp_images/windspeed.png"));
		windspeedImage.setBounds(220, 500, 74, 66);
		frame.add(windspeedImage);
		
		windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
		windspeedText.setBounds(310, 500, 85, 55);
		windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
		frame.add(windspeedText);
		
		searchButton = new JButton(loadImage("src/weatherapp_images/search.png"));
		searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		searchButton.setBounds(375, 13, 47, 45);
		searchButton.setBackground(Color.white);
		searchButton.setFocusable(false);
		searchButton.addActionListener(this);
		
		frame.add(searchButton);
		
	}
	
	private ImageIcon loadImage(String resourcePath) {
		try {
			BufferedImage image = ImageIO.read(new File(resourcePath));
			return new ImageIcon(image);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Could not find resource");
		return null;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == searchButton) {
			
			// get location from user
			String userInput = searchTextField.getText();
			
			if(userInput.replaceAll("\\s", "").length() <= 0) {
				return;
			}
			
			//retrive weather data
			weatherData = WeatherApp.getWeatherData(userInput);
			
			//update weather image
			String weatherCondition = (String) weatherData.get("weather_condition");
			
			switch(weatherCondition) {
				case "Clear":
					weatherConditionImage.setIcon(loadImage("src/weatherapp_images/clear.png"));
					break;
				case "Cloudy":
					weatherConditionImage.setIcon(loadImage("src/weatherapp_images/cloudy.png"));
					break;
				case "Rain":
					weatherConditionImage.setIcon(loadImage("src/weatherapp_images/rain.png"));
					break;
				case "Snow":
					weatherConditionImage.setIcon(loadImage("src/weatherapp_images/snow.png"));
					break;
			}
			//update temperature
			double temperature = (double) weatherData.get("temperature");
			temperatureText.setText(temperature + " C");
			
			//update condition text
			weatherConditionsDesc.setText(weatherCondition);
			
			//update humidity text
			long humidity = (long) weatherData.get("humidity");
			humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");
			
			//update windspeed
			double windspeed = (double) weatherData.get("windspeed");
			windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
		}
	}
}
