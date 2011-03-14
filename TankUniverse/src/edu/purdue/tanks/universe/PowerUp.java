package edu.purdue.tanks.universe;

import javax.microedition.khronos.opengles.GL10;

public class PowerUp extends GameObject {

	public PowerUp(int type) {
		super(type);
	}

	@Override
	public void draw(GL10 gl, int[] imageResources, float playerPosX, float playerPosY) {
		//super.draw();
	}

	@Override
	public boolean isCollision(int tankx, int tanky) {
		return super.isCollision(tankx, tanky);
	}

	@Override
	public void update(double time) {
		super.update(time);
	}

}
