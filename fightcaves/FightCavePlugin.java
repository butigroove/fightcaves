/*
 * Copyright 2019, ProjectPact
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.fightcaves;

import javax.swing.SwingUtilities;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import java.util.Map;

@PluginDescriptor(
	name = "Fight Caves"
)
public class FightCavePlugin extends Plugin
{

	private Console console;
	private boolean consoleRunning = false;

	@Inject
	private Client client;

	private JadAttack attack;

	private Map<Integer, int[]> map = Waves.waveMapping();

	private boolean lastAttackMage = false;

	@Schedule(
		period = 600,
		unit = ChronoUnit.MILLIS
	)
	public void update()
	{
		if (client == null || !consoleRunning || client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		} else {
			try {
				NPC jad = findJad();
				if (jad != null) {
					if(console != null)
						console.clear();

					if (jad.getAnimation() == JadAttack.MAGIC.getAnimation()) {
						log("PRAY MAGE! * ");
						lastAttackMage = true;
					} else if(jad.getAnimation() == JadAttack.RANGE.getAnimation()){
						log("PRAY RANGE!  --> ");
						lastAttackMage = false;
					} else {
						if(lastAttackMage){
							log("PRAY MAGE! * ");
						} else {
							log("PRAY RANGE!  --> ");
						}
					}
				}
			} catch (NullPointerException e){
				// e.printStackTrace();
			}
		}
	}

	private NPC findJad()
	{
		NPC result = null;

		for (NPC npc : client.getNpcs())
		{
			if(npc != null) {
				if (npc.getName().toLowerCase().contains("tztok") && npc.getName().toLowerCase().contains("jad")) {
					result = npc;
					break;
				}
			}
		}

		return result != null ? result : null;
	}

	JadAttack getAttack()
	{
		return attack;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		int wave = 0;
		String msg = event.getMessage();

		if (msg.contains("Wave:")) {
		FightCavePlugin fc = this;

			if(!consoleRunning) {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							console = new Console(fc);
							console.init();
							log("Initializing console...");
							setConsoleRunning(true);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if(console != null)
				console.clear();

			wave = Integer.parseInt(msg.split("Wave:")[1].split("<")[0].trim());
			int nextWave = (wave + 1);

			log("************* Starting Wave: " + wave + " *************");
			log("Current Wave NPCs:");
			int i = 1;
			for (int id : map.get(wave)) {
				log(i + ") " + id);
				i++;
			}
			log("");
			log("-------------------- NEXT WAVE (" + nextWave + ") --------------------");
			log("");

			/**
			 * Info
			 */
			log("INFO:");

			if (nextWave < 7) {
				log("No need to pray next round!");
			}
			if (nextWave >= 7 && nextWave <= 14) {
				log("Protect from Range next round!");
			} else if (nextWave >= 15 && nextWave <= 21) {
				log("Protect from Melee next round if needed!");
			} else if (nextWave >= 22 && nextWave <= 29) {
				log("Protect from Range next round!");
			} else if(nextWave == 30){
				log("Protect from Melee next round if needed!");
			} else if (nextWave >= 31 && nextWave < 63) {
				log("Protect from Mage next round!");
			} else if (nextWave == 63) {
				log("Jad is next! Find where the orange 360 spawns, that will be Jad's spawning location!");
			}
			log("");

			/**
			 * Tips
			 */
			log("TIP:");

			if (nextWave == 30) {
				log("Drink a Saradomin brew and turn on Protect from Magic after killing the last level 180.");
			} else if (nextWave == 31) {
				log("Never turn off Protect from Magic unless you are 100% sure there are no level 360s around.");
			} else if (nextWave == 37) {
				log("Make sure you have high health before moving onto the next wave.");
			} else if (nextWave == 38) {
				log("Keep praying Protect from Magic as you want to prioritise blocking the level 360's attacks. ");
				log("Finish off the level 90 first to avoid as much damage possible.");
			} else if (nextWave == 45) {
				log("You may take a lot of damage here from the two level 90s; keep a good watch on your health.");
			} else if (nextWave == 46) {
				log("Try to get the level 180 trapped behind the level 360 or use Italy Rock.");
			} else if (nextWave == 49) {
				log("You can use the level 45 to prevent getting hit by the level 180. This is possible by putting it between you and the level 180.");
			} else if (nextWave == 53) {
				log("Try to trap the level 180 behind the level 360 or 90, or trap it at Italy Rock.");
				log("Try to kill the level 90 first unless you can't trap the level 180; in that case, trap and kill the level 180 first.");
			} else if (nextWave == 60) {
				log("Like Wave 45, you may take a lot of damage from the two level 90s, so keep a good watch on your health.");
			} else if (nextWave == 63) {
				log("Keep note where the orange 360 spawns, as this is where TzTok-Jad will spawn.");
				log("Make sure you have full health and Prayer before killing the last level 360.");
			} else {
				log("N/A");
			}
			log("");

			if (wave + 1 < 64) {
				log("Next Wave NPCs:");
				i = 1;
				for (int id : map.get(nextWave)) {
					log(i + ") " + id);
					i++;
				}
			}
			log("");
			log("*********************************************");

		}
	}

	private void log(String message){
		if(consoleRunning){
			if(console != null) {
				console.log(message);
			}
		}
	}

	public void setConsoleRunning(boolean value){
		consoleRunning = value;
	}

}
