package com.example.amazonpolly;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.Voice;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

@SpringBootApplication
public class AmazonPollyApplication {

	private final AmazonPollyClient polly;
	private final Voice voice;
	private static final String SAMPLE = "Congratulations. You have successfully built this working demo \n" + 
			"//	of Amazon Polly in Java.  Have fun building voice enabled apps with Amazon Polly.";
	private static final String SAMPLE_HINDI = "Badhai ho . aapne Amazon polly ka demo safalta purvak banaya hai. Ab aap sound wali apps bna sakte hai Amazon poly se.";
	private static final String ADITI = "Aditi";
	private static final String EMMA ="Emma";

	public AmazonPollyApplication(Region region) {
		// create an Amazon Polly client in a specific region
		BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAVIQJFL4GNKHAIM26", "Hf+yKvnYpYbMCK1LW20pcHm7Q3QucIcmNtPREb6U");
		polly = new AmazonPollyClient(credentials, new ClientConfiguration());
		polly.setRegion(region);
		
		// Create describe voices request.
		DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

		// Synchronously ask Amazon Polly to describe available TTS voices.
		DescribeVoicesResult describeVoicesResult = polly.describeVoices(describeVoicesRequest);
		voice = describeVoicesResult.getVoices().stream().filter(speaker -> speaker.getName().equals(ADITI)).findFirst().get();
	}

	public InputStream synthesize(String text, OutputFormat format) throws IOException {
		SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(text).withVoiceId(voice.getId())
				.withOutputFormat(format);
		SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);

		return synthRes.getAudioStream();
	}

	public static void main(String[] args) throws IOException, JavaLayerException {
		//SpringApplication.run(AmazonPollyApplication.class, args);
		// create the test class
		AmazonPollyApplication application = new AmazonPollyApplication(Region.getRegion(Regions.AP_SOUTH_1));
		// get the audio stream
		InputStream speechStream = application.synthesize(SAMPLE_HINDI, OutputFormat.Mp3);

		// create an MP3 player
		AdvancedPlayer player = new AdvancedPlayer(speechStream,
				javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());

		player.setPlayBackListener(new PlaybackListener() {
			@Override
			public void playbackStarted(PlaybackEvent evt) {
				System.out.println("Playback started");
				System.out.println(SAMPLE_HINDI);
			}

			@Override
			public void playbackFinished(PlaybackEvent evt) {
				System.out.println("Playback finished");
			}
		});

		// play it!
		player.play();
	}

}
