/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.ArrayList;
import java.util.Random;

public class GameScreen implements Screen {
  	final Drop game;

	Texture shoot;
        Texture laser;
	Texture spaceShip;
        Texture alienShip;
        Texture alienShip2;
        Texture alienShip3;
        Texture alienShip4;
        Texture alienShip5;
        ArrayList<Texture> alienShips;
        Texture background;
        
	Sound dropSound;
	Music rainMusic;
        Sound missilSound;
        Sound laserSound;
	OrthographicCamera camera;
	Rectangle spriteSpaceShip;
        Rectangle spriteAlienShip;
	Array<Rectangle> alienShipShoot;
        Array<Rectangle> spaceShipShoot;

	long lastLaserTime;
	long lastShootTime;
	int vidas = 3;
        int dropsSpilled;
        int movimiento = 3;
        int kills = 0;
        int laserSpeed = 150;
        int laserShootSpeed = 800000000;

        Texture explosion;
        
	public GameScreen(final Drop gam) {
            this.game = gam;

            // load the images for the droplet and the bucket, 64x64 pixels each
            shoot = new Texture(Gdx.files.internal("spaceMissile.png"));
            laser = new Texture(Gdx.files.internal("laserBlue.png"));
            spaceShip = new Texture(Gdx.files.internal("spaceShip.png"));
            
//            alienShip = new Texture(Gdx.files.internal("alienShip0.png"));
//            alienShip2 = new Texture(Gdx.files.internal("alienShip1.png"));
//            alienShip3 = new Texture(Gdx.files.internal("alienShip2.png"));
//            alienShip4 = new Texture(Gdx.files.internal("alienShip3.png"));
//            alienShip5 = new Texture(Gdx.files.internal("alienShip4.png"));
            
            alienShips = new ArrayList<Texture>();
            
            for (int i = 0; i < 5; i++) 
                alienShips.add(new Texture(Gdx.files.internal("alienShip" + i + ".png")));
            
            background = new Texture(Gdx.files.internal("background.png"));
            explosion = new Texture(Gdx.files.internal("explosion.png"));

            // load the drop sound effect and the rain background "music"
            dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
            rainMusic = Gdx.audio.newMusic(Gdx.files.internal("ost.mp3"));
            missilSound = Gdx.audio.newSound(Gdx.files.internal("missil.mp3"));
            laserSound = Gdx.audio.newSound(Gdx.files.internal("laser.mp3"));

            // create the camera and the SpriteBatch
            camera = new OrthographicCamera();
            camera.setToOrtho(false, 800, 600);

            // create a Rectangle to logically represent the bucket
            spriteSpaceShip = new Rectangle();
            spriteSpaceShip.x = 800 / 2 - 64 / 2; // center the bucket horizontally
            spriteSpaceShip.y = 20; // bottom left corner of the bucket is 20 pixels above
            spriteSpaceShip.width = 64;
            spriteSpaceShip.height = 64;

            // create a Rectangle to logically represent the bucket
            spriteAlienShip = new Rectangle();
            spriteAlienShip.x = 800 / 2 - 64 / 2; // center the bucket horizontally
            spriteAlienShip.y = 500; // bottom left corner of the bucket is 20 pixels above
            spriteAlienShip.width = 64;
            spriteAlienShip.height = 64;

            // create the raindrops array and spawn the first raindrop
            alienShipShoot = new Array<Rectangle>();
            spaceShipShoot = new Array<Rectangle>();

	}

	private void alienShipShoot() {
            Rectangle laser = new Rectangle();
            laser.x = spriteAlienShip.x + 27;
            laser.y = 500;
            laser.width = 64;
            laser.height = 64;
            alienShipShoot.add(laser);
            lastLaserTime = TimeUtils.nanoTime();
	}
        
        private void spaceShipShoot() {
            Rectangle shoot = new Rectangle();
            
            shoot.x = spriteSpaceShip.x + 22;
            shoot.y = 50;
            shoot.width = 64;
            shoot.width = 64;
            spaceShipShoot.add(shoot);
            lastShootTime = TimeUtils.nanoTime();
        }

	@Override
	public void render(float delta) {
            // clear the screen with a dark blue color. The
            // arguments to glClearColor are the red, green
            // blue and alpha component in the range [0,1]
            // of the color to be used to clear the screen.
            Gdx.gl.glClearColor(0, 0, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // tell the camera to update its matrices.
            camera.update();

            // tell the SpriteBatch to render in the
            // coordinate system specified by the camera.
            game.batch.setProjectionMatrix(camera.combined);

            // begin a new batch and draw the bucket and
            // all drops
            this.textureSet();

            // process user input
            
            this.spaceShipMove();
            this.alienShipMove();

            this.refreshShoot();
            this.refreshLaser();
            
	}
        
        public void refreshShoot () {
            Iterator<Rectangle> iterator = spaceShipShoot.iterator();
            while (iterator.hasNext()) {
                Rectangle shoot = iterator.next();
                shoot.y += 400 * Gdx.graphics.getDeltaTime();

                if (shoot.overlaps(spriteAlienShip)) {
                    iterator.remove();
                    kills++;
                    laserSpeed += 100;
                    laserShootSpeed -= 100000000;

                    
                    game.batch.begin();
                    game.batch.draw(explosion, spriteAlienShip.x-70, spriteAlienShip.y-70);
                    game.batch.end();
                    
                    spriteAlienShip.x = 0;

                    if (movimiento < 0)
                        movimiento -= 2;
                    else
                        movimiento += 2;
                    
                    if (kills == 5)
                        game.setScreen(new WinScreen(game));
                }
            }
        }
        
        public void refreshLaser () {
            Iterator<Rectangle> iterator2 = alienShipShoot.iterator();
            while (iterator2.hasNext()) {
                Rectangle laser = iterator2.next();
                laser.y -= laserSpeed * Gdx.graphics.getDeltaTime();

                if (laser.overlaps(spriteSpaceShip)) {
                    iterator2.remove();
                    vidas--;
                    if (vidas == 0) {
                        game.setScreen(new GameOverScreen(game));
                    }
                        
                }
            }
        }
        
        public void alienShipMove () {
            Random rand = new Random();

            if (rand.nextInt(100) > 98)
                movimiento *= -1;

            spriteAlienShip.x += movimiento;

            if (spriteAlienShip.x < 0) {
                spriteAlienShip.x = 0;
                movimiento *= -1;
            }
            if (spriteAlienShip.x > 800 - 64) {
                spriteAlienShip.x = 800 - 64;
                movimiento *= -1;
            }

            if (TimeUtils.nanoTime() - lastLaserTime > laserShootSpeed) {
                alienShipShoot();
                laserSound.play((float) 0.05);
            }
        }
        
        public void spaceShipMove () {
            if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                spriteSpaceShip.x = touchPos.x - 64 / 2;
            }
            if (Gdx.input.isKeyPressed(Keys.LEFT))
                spriteSpaceShip.x -= 400 * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Keys.RIGHT))
                spriteSpaceShip.x += 400 * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Keys.SPACE) && TimeUtils.nanoTime() - lastShootTime > 500000000) {
                spaceShipShoot();
                missilSound.play((float) 0.1);
            }


            // make sure the bucket stays within the screen bounds
            if (spriteSpaceShip.x < 0)
                    spriteSpaceShip.x = 0;
            if (spriteSpaceShip.x > 800 - 64)
                    spriteSpaceShip.x = 800 - 64;
        }
        
        public void textureSet () {
            game.batch.begin();
            game.batch.draw(background, 0, 0);
            game.font.draw(game.batch, "Vidas: " + vidas + "/3", 30, 30);
            game.batch.draw(spaceShip, spriteSpaceShip.x, spriteSpaceShip.y);
            
            game.batch.draw(alienShips.get(kills), spriteAlienShip.x, spriteAlienShip.y);
            
//            if (kills == 0) 
//                game.batch.draw(alienShip, spriteAlienShip.x, spriteAlienShip.y);
//            
//            if (kills == 1)
//                game.batch.draw(alienShip2, spriteAlienShip.x, spriteAlienShip.y);
//            
//            if (kills == 2)
//                game.batch.draw(alienShip3, spriteAlienShip.x, spriteAlienShip.y);
//            
//            if (kills == 3)
//                game.batch.draw(alienShip4, spriteAlienShip.x, spriteAlienShip.y);
//            
//            if (kills == 4)
//                game.batch.draw(alienShip5, spriteAlienShip.x, spriteAlienShip.y);
                
            for (Rectangle raindrop : alienShipShoot) {
                    game.batch.draw(laser, raindrop.x, raindrop.y);
            }
            for (Rectangle raindrop : spaceShipShoot) {
                    game.batch.draw(shoot, raindrop.x, raindrop.y);
            }

            game.batch.end();
        }

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		shoot.dispose();
		spaceShip.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

}