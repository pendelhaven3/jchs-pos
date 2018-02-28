package com.pj.magic;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.pj.magic.gui.MagicFrame;

@SpringBootApplication
public class JchsPos {

	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UISettings.initialize();
                
                ConfigurableApplicationContext context = new SpringApplicationBuilder(JchsPos.class).headless(false).run(args);
                
                MagicFrame frame = context.getBean(MagicFrame.class);
                frame.setVisible(true);
            }
        });
	}
	
}
