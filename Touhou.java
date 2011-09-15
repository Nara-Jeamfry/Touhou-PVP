import jgame.*;
import jgame.platform.*;

/** Tutorial example 1: a minimal program.  A "bare skeleton" program
 * displaying a moving text "hello world".
 *
 * In order to run as both applet and application, you need to define a main()
 * method (this is the entry point for an application) and a parameterless
 * constructor (this is the entry point for an applet).  We use a second
 * constructor with a size parameter to initialise the engine as an
 * application.
 */
public class Touhou extends JGEngine {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    int PJx, PJy;
   
    public static void main(String [] args) {
        // We start the engine with a fixed window size (which happens to
        // be twice the size of the defined playfield, scaling the playfield
        // by a factor 2).  Normally, you'd want this size to be configurable,
        // for example by means of command line parameters.
        new Touhou(new JGPoint(640,480));
    }

    /** The parameterless constructor is called by the browser, in case we're
     * an applet. */
    public Touhou() {
        // This inits the engine as an applet.
        initEngineApplet();
    }

    /** We use a separate constructor for starting as an application. */
    public Touhou(JGPoint size) {
        // This inits the engine as an application.
        initEngine(size.x,size.y);
    }

    /** This method is called by the engine when it is ready to intialise the
     * canvas (for an applet, this is after the browser has called init()).
     * Note that applet parameters become available here and not
     * earlier (don't try to access them from within the parameterless
     * constructor!).  Use isApplet() to check if we started as an applet.
     */
    public void initCanvas() {
        // The only thing we need to do in this method is to tell the engine
        // what canvas settings to use.  We should not yet call any of the
        // other game engine methods here!
        setCanvasSettings(
            20,  // width of the canvas in tiles
            15,  // height of the canvas in tiles
            16,  // width of one tile
            16,  // height of one tile
                 //    (note: total size = 20*16=320  x  15*16=240)
            null,// foreground colour -> use default colour white
            null,// background colour -> use default colour black
            null // standard font -> use default font
        );
    }

    /** This method is called when the engine has finished initialising and is
     * ready to produce frames.  Note that the engine logic runs in its own
     * thread, separate from the AWT event thread, which is used for painting
     * only.  This method is the first to be called from within its thread.
     * During this method, the game window shows the intro screen. */
    public void initGame() {
        // We can set the frame rate, load graphics, etc, at this point.
        // (note that we can also do any of these later on if we wish)
        setFrameRate(
            35,// 35 = frame rate, 35 frames per second
            2  //  2 = frame skip, skip at most 2 frames before displaying
               //      a frame again
        );
       
        new MyObject();
        setGameState("title");
    }
   
    public void startTitle()
    {
        removeObjects(null, 0);
    }
   
    public void paintFrameTitle() {
        drawString("Title state. Press space to go to InGame",viewWidth()/2,90,0);
    }
   
    public void doFrameTitle() {
        if (getKey(' ')) {
            // ensure the key has to be pressed again to register
            clearKey(' ');
            // Set both StartGame and InGame states simultaneously.
            // When setting a state, the state becomes active only at the
            // beginning of the next frame.
            setGameState("StartGame");
            addGameState("InGame");
            // set a timer to remove the StartGame state after a few seconds,
            // so only the InGame state remains.
            new JGTimer(
                180, // number of frames to tick until alarm
                true, // true means one-shot, false means run again
                      // after triggering alarm
                "StartGame" // remove timer as soon as the StartGame state
                            // is left by some other circumstance.
                            // In particular, if the game ends before
                            // the timer runs out, we don't want the timer to
                            // erroneously trigger its alarm at the next
                            // StartGame.
            ) {
                // the alarm method is called when the timer ticks to zero
                public void alarm() {
                    removeGameState("StartGame");
                }
            };
        }
    }
   
    /** The StartGame state is just for displaying a start message. */
    public void paintFrameStartGame() {
        drawString("We are in the StartGame state.",pfWidth()/2,90,0);
    }
   
    /** Game logic is done here.  No painting can be done here, define
    * paintFrame to do that. */
    public void doFrame() {
        moveObjects(
                null,// object name prefix of objects to move (null means any)
                0    // object collision ID of objects to move (0 means any)
            );
        if(getKey(KeyEsc)) return;
    }

    /** Any graphics drawing beside objects or tiles should be done here.
     * Usually, only status / HUD information needs to be drawn here. */
    public void paintFrameInGame() {
        setColor(JGColor.yellow);
        // Draw a text that moves around in a circle.
        // Note: viewWidth returns the width of the view;
        //       viewHeight the height.
        drawString("X:"+PJx+" , Y"+PJy+",",
            viewWidth()/16, // xpos
            viewHeight()/9, // ypos
            -1 // the text alignment
              // (-1 is left-aligned, 0 is centered, 1 is right-aligned)
        );
    }
   
    /** Our user-defined object. */
    class MyObject extends JGObject {

        private boolean focused;
       
        /** Constructor. */
        MyObject () {
            // Initialise game object by calling an appropriate constructor
            // in the JGObject class.
            super(
                "myobject",// name by which the object is known
                false,//true means add a unique ID number after the object name.
                     //If we don't do this, this object will replace any object
                     //with the same name.
                random(0,pfWidth()),  // X position
                random(0,pfHeight()), // Y position
                1, // the object's collision ID (used to determine which classes
                   // of objects should collide with each other)
                null // name of sprite or animation to use (null is none)
            );
            // Give the object an initial speed in a random direction.
        }

        /** Update the object. This method is called by moveObjects. */
        public void move() {
            // A very "classic" behaviour:
            // bounce off the borders of the screen.
            if (getKey(KeyLeft)) xspeed = -3;
            else if(getKey(KeyRight)) xspeed = 3;
            else xspeed=0;
            if (getKey(KeyUp)) yspeed = -3;
            else if (getKey(KeyDown)) yspeed = 3;
            else yspeed = 0;
            // xspeed and yspeed are added to x and y automatically at the end
            // of the move() method.
            PJx = (int) this.x;
            PJy = (int) this.y;
            if(getKey(KeyShift))
            {
                focused=true;
            }
            else
                focused=false;
            if(this.focused)
            {
                xspeed*=0.4;
                yspeed*=0.4;
            }
            if(getKey('z')) {
                new Bullet(this.x, this.y);
                clearKey('z');
            }
        }

        /** Draw the object. */
        public void paint() {
            // Draw a yellow ball
            setColor(JGColor.blue);
            drawOval(x,y,16,16,true,true);
        }

    } /* end class MyObject */
   
    class Bullet extends JGObject {
        Bullet (double positionX, double positionY) {
            // Initialise game object by calling an appropriate constructor
            // in the JGObject class.
            super(
                "bullet",// name by which the object is known
                true,//true means add a unique ID number after the object name.
                     //If we don't do this, this object will replace any object
                     //with the same name.
                positionX,  // X position
                positionY, // Y position
                1, // the object's collision ID (used to determine which classes
                   // of objects should collide with each other)
                null // name of sprite or animation to use (null is none)
            );
            // Give the object an initial speed in a random direction.
        }
        public void paint() {
            // Draw a yellow ball
            setColor(JGColor.blue);
            drawRect()
        }
    }
}