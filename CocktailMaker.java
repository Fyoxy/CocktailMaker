import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import org.osbot.rs07.api.Widgets;
import org.osbot.rs07.api.Settings;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.Keyboard;

import java.awt.*;


@ScriptManifest(author = "Fyoxy", info = "Makes blurberry specials at tree gnome village", name = "Blurberry special maker", version = 1, logo = "")
public class CocktailMaker extends Script {

    /*
     *   YOU NEED TO HAVE COINS 
     *   AND A COCKTAIL MIXER IN 
     *   YOUR INVENTORY FOR THIS TO WORK
     */

    // Variables for time
    private long startTime;
    private long runTime = System.currentTimeMillis() - startTime;

    // Graphics variables
	private Font titleFont = new Font("Sans-Serif", Font.BOLD, 14);
	private String state = "State: Idle"; // Script state

	// Areas
    private Area shop = new Area(2493, 3487, 2488, 3489).setPlane(1);
    private Area bank = new Area(2450, 3480, 2448, 3482).setPlane(1);

    // Variables for script
    private int profit = 0;

    @Override	
	public void onStart() {
        log("Script is STARTING");

        // Check if there are any coins in the inventory
        if (!getInventory().contains("Coins")) stop(true);
        log("Need coins to start script");

        startTime = System.currentTimeMillis(); // Get current time
	}

	@Override
	public int onLoop() throws InterruptedException {

        /* The ID for blurberry special is 2064
         * If the inventory contains 19 specials go to bank
         */
        if (getInventory().getAmount(2064) >= 19)
        {
            if (bank.contains(myPlayer()))
            {
                getBank().open();
                new ConditionalSleep(5000, 300) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return getBank().isOpen();
                    }
                }.sleep();
                if (getBank().isOpen())
                {
                    // Calculating the total cost of all the specials
					for (Item x : getInventory().getItems()) {
                        // Some items in the inventory might not have a GE price
						if (x != null) {
                            // If the item does not have a price it ends up in catch
							try {
								profit += getGrandExchange().getOverallPrice(x.getId());
							} catch (Exception e) {
								// No need to handle the error
							}
						}
                    }
                    // Deposit all specials
                    getBank().depositAll(2064);
                }
            }
            else
            {
                getWalking().webWalk(bank);
            }
        }
        else
        {
            if (shop.contains(myPlayer()))
            {
                if (getInventory().contains("Vodka") && 
                getInventory().contains("Gin") && 
                getInventory().contains("Brandy") && 
                getInventory().contains("Orange") && 
                getInventory().contains("Lemon") &&
                getInventory().getAmount("Lemon") == 2)
                {
                    state = "Creating mix";
                    getInventory().getItem("Cocktail shaker").interact("Mix-cocktail");
                    RS2Widget cocktailMenu = getWidgets().get(436, 3, 6);

                    // Sleeping until the options widget appears
                    new ConditionalSleep(5000, 300) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return getWidgets().get(436, 4) != null;
                        }
                    }.sleep();

                    if (cocktailMenu != null)
                    {
                        cocktailMenu.interact("Create");
                        new ConditionalSleep(2000, 200) {
                            @Override
                            public boolean condition() throws InterruptedException {
                                return cocktailMenu.interact("Create");
                            }
                        }.sleep();
                        RS2Widget create = getWidgets().get(436, 9);
                        if (create != null)
                        {
                            create.interact("Create");
                            new ConditionalSleep(2000, 200) {
                                @Override
                                public boolean condition() throws InterruptedException {
                                    return getInventory().contains("Mixed special");
                                }
                            }.sleep();
                        }
                    }
                    
                }
                else if (getInventory().contains("Mixed special"))
                {
                    state = "Finishing cocktail";
                    if (getInventory().contains("Lime"))
                    {
                        RS2Widget limeSlices = getWidgets().get(270, 14, 29);
                        
                        
                        if (limeSlices != null)
                        {
                            limeSlices.interact("Cut");
                            new ConditionalSleep(2000, 200) {
                                @Override
                                public boolean condition() throws InterruptedException {
                                    return getInventory().contains("Lime slices");
                                }
                            }.sleep();
						}
						else
						{
							getInventory().getItem("Knife").interact("Use");
                        	new ConditionalSleep(2000, 200) {
                        	    @Override
                        	    public boolean condition() throws InterruptedException {
                        	        return getInventory().isItemSelected();
                        	    }
                        	}.sleep();
                        	getInventory().getItem("Lime").interact("Use");
                        	new ConditionalSleep(5000, 800) {
                        	    @Override
                        	    public boolean condition() throws InterruptedException {
                        	        return getWidgets().get(270, 14) != null;
                        	    }
                        	}.sleep();
						}
                    }
                    else if (getInventory().contains("Lemon"))
                    {
                        RS2Widget lemonDices = getWidgets().get(270, 15, 29);
                        
                        
                        if (lemonDices != null)
                        {
                            getKeyboard().typeKey('2');
                            new ConditionalSleep(2000, 200) {
                                @Override
                                public boolean condition() throws InterruptedException {
                                    return getInventory().contains("Lemon chunks");
                                }
                            }.sleep();
						}
						else
						{
							getInventory().getItem("Knife").interact("Use");
                        	new ConditionalSleep(2000, 200) {
                        	    @Override
                        	    public boolean condition() throws InterruptedException {
                        	        return getInventory().isItemSelected();
                        	    }
                        	}.sleep();
                        	getInventory().getItem("Lemon").interact("Use");
                        	new ConditionalSleep(5000, 800) {
                        	    @Override
                        	    public boolean condition() throws InterruptedException {
                        	        return getWidgets().get(270, 14) != null;
                        	    }
                        	}.sleep();
						}
                    }
                    else if (getInventory().contains("Orange"))
                    {
                        RS2Widget orangeDices = getWidgets().get(270, 15, 29);
                
                        
                        if (orangeDices != null)
                        {
                            getKeyboard().typeKey('2');
                            new ConditionalSleep(2000, 200) {
                                @Override
                                public boolean condition() throws InterruptedException {
                                    return getInventory().contains("Orange chunks");
                                }
                            }.sleep();
                        }
                        else
                        {
                            getInventory().getItem("Knife").interact("Use");
                            new ConditionalSleep(2000, 200) {
                                @Override
                                public boolean condition() throws InterruptedException {
                                    return getInventory().isItemSelected();
                                }
                            }.sleep();
                            getInventory().getItem("Orange").interact("Use");
                            new ConditionalSleep(5000, 800) {
                                @Override
                                public boolean condition() throws InterruptedException {
                                    return getWidgets().get(270, 14) != null;
                                }
                            }.sleep();
                        }
                    }
                    else if (getInventory().contains("Orange chunks") &&
                             getInventory().contains("Lemon chunks") && 
                             getInventory().contains("Lime slices"))
                    {
                        getInventory().interact("Pour", "Mixed special");
                        new ConditionalSleep(2000, 200) {
                            @Override
                            public boolean condition() throws InterruptedException {
                                return !getInventory().contains("Mixed special");
                            }
                        }.sleep();
                    }
                    else
                    {
                        state = "Buying second mats";
                        NPC funch = getNpcs().closest(16);
                        if (funch != null)
                        {
                            funch.interact("Trade");
                            new ConditionalSleep(5000, 300) {
                                @Override
                                public boolean condition() throws InterruptedException {
                                    return getStore().isOpen();
                                }
                            }.sleep();
                            if (getStore().isOpen())
                            {
								log(StoreCheck());
								if (StoreCheck())
								{
									if (!getInventory().contains("Lime")) getStore().buy("Lime", 1);

									if (!getInventory().contains("Equa leaves")) getStore().buy("Equa leaves", 1);

									if (!getInventory().contains("Brandy")) getStore().buy("Brandy", 1);

									if (!getInventory().contains("Lemon")) getStore().buy("Lemon", 1);

									if (!getInventory().contains("Orange")) getStore().buy("Orange", 1);

                            		getStore().close();
								}
								else if (!StoreCheck())
								{
									state = "Hopping";
									getWorlds().hopToP2PWorld();
									log(getWorlds().hopToP2PWorld());
								}
                            }
                        } 
                    }
                    
                }
                else
                {
                    state = "Buying first mats";
                    NPC funch = getNpcs().closest(16);
                    if (funch != null)
                    {
                        funch.interact("Trade");
                        new ConditionalSleep(5000, 300) {
                            @Override
                            public boolean condition() throws InterruptedException {
                                return getStore().isOpen();
                            }
                        }.sleep();
                        if (getStore().isOpen())
                        {
							log(StoreCheck());
							if (StoreCheck())
							{
								if (!getInventory().contains("Vodka")) getStore().buy("Vodka", 1);

								if (!getInventory().contains("Gin")) getStore().buy("Gin", 1);

								if (!getInventory().contains("Brandy")) getStore().buy("Brandy", 1);
								
								if (getInventory().contains("Lemon") && getInventory().getAmount("Lemon") == 1) getStore().buy("Lemon", 1);
								else if (!getInventory().contains("Lemon") && getInventory().getAmount("Lemon") == 0) getStore().buy("Lemon", 2);

								if (!getInventory().contains("Orange")) getStore().buy("Orange", 1);

								if (!getInventory().contains("Cocktail glass")) getStore().buy("Cocktail glass", 1);

                            	getStore().close();
							}
							else if (!StoreCheck())
							{
								state = "Hopping";
								getWorlds().hopToP2PWorld();
								log(getWorlds().hopToP2PWorld());
							}
							
                        }
                    } 
                }

                
            }
            else
            {
                state = "Going to shop";
                getWalking().webWalk(shop);
            }
        }

		return 300;
	}

	@Override
	public void onExit() {
		log("Script STOPS");
    }

    @Override
    public void onPaint(Graphics2D t) {
		/* 	t for informative text 
			g for other graphics	*/

        // Informative text
        t.setFont(titleFont);
        t.setColor(Color.BLUE);
		t.drawRect(mouse.getPosition().x - 3, mouse.getPosition().y - 3, 6, 6);
		t.drawString(state, 10, 265);
		t.drawString("Profit: " + profit, 10, 280); 
		//t.drawString("Death count: " + deathCount, 10, 310);
        t.drawString("Fyoxy's Cocktail maker", 10, 250);

        // Draw the total time ran
        runTime = System.currentTimeMillis() - startTime;
		t.drawString(formatTime(runTime), 8, 334);

	}

    public final String formatTime(final long ms){
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60; m %= 60; h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
    
    // Checks if the store still has items in stock
    public boolean StoreCheck() {

		if (getStore().getAmount("Gin") == 0 || 
		getStore().getAmount("Vodka") == 0 || 
		getStore().getAmount("Brandy") == 0 || 
		getStore().getAmount("Lemon") <= 1 || 
		getStore().getAmount("Lime") == 0 || 
		getStore().getAmount("Orange") == 0 || 
		getStore().getAmount("Cocktail glass") == 0 || 
		getStore().getAmount("Equa leaves") == 0) return false;
		
		return true;

	}
}