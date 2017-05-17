package BlackAndWhite;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class musicPlay {

	File bgm = new File("bgm.wav");
	File click = new File("click.wav");
	AudioInputStream stream1, stream2;
	AudioFormat format1, format2;
	DataLine.Info info1, info2;
	Clip clip1, clip2;

	public musicPlay() {
		try {
			stream1 = AudioSystem.getAudioInputStream(bgm);
			stream2 = AudioSystem.getAudioInputStream(click);
			format1 = stream1.getFormat();
			format2 = stream2.getFormat();

			info1 = new DataLine.Info(Clip.class, format1);
			info2 = new DataLine.Info(Clip.class, format2);

			clip1 = (Clip) AudioSystem.getLine(info1);
			clip2 = (Clip) AudioSystem.getLine(info2);

			clip1.open(stream1);
			clip2.open(stream2);

			// clip.start();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
