package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

class MainCharacter extends Walker {

    private int health;
    private int armour;
    private int ammo;
    private AttachedImage astronautImage;
    private AttachedImage arm;

    private float dashCooldown = 0.0f;
    private boolean canDash = false;

    private boolean dPressed = false;
    private boolean aPressed = false;
    private int movingDir = 0;
    private boolean bothKeysPressed = false;

    private boolean jumping = false;
    private int extraJumps;
    private int extraJumpsLimit = 2;
    private boolean canJump = false;
    private double jumpPressedRemember;
    private double jumpPressedRememberTime = 0.15f;

    MainCharacter(Shape shape, int health, int armour, int ammo) {
        super(Game.getWorld(), shape);
        this.health = health;
        this.armour = armour;
        this.ammo = ammo;
        this.extraJumps = this.extraJumpsLimit;
        this.setName("Player");

        this.setGravityScale(9.81f);

        this.astronautImage = new AttachedImage(this, new BodyImage("data/Idle.png", 2.7f), 1, 0, new Vec2(0, 0));
        this.arm = new AttachedImage(this, new BodyImage("data/Arm.png", 2), 0.19f, 0, new Vec2(-0.15f, 0));
    }

    void changeImages(String path) {
        if (this.astronautImage.isFlippedHorizontal()) {
            this.removeAllImages();
            this.astronautImage = new AttachedImage(this, new BodyImage("data/" + path, 2.2f), 1, 0, new Vec2(0, 0));
            this.arm = new AttachedImage(this, new BodyImage("data/Arm.png", 2), 0.19f, 0, new Vec2(-0.15f, 0));

            this.astronautImage.flipHorizontal();
            this.arm.flipHorizontal();
        } else if (!this.astronautImage.isFlippedHorizontal()) {
            this.removeAllImages();
            this.astronautImage = new AttachedImage(this, new BodyImage("data/" + path, 2.2f), 1, 0, new Vec2(0, 0));
            this.arm = new AttachedImage(this, new BodyImage("data/Arm.png", 2), 0.19f, 0, new Vec2(-0.15f, 0));
        }
    }

    AttachedImage getAstronautImage() {
        return astronautImage;
    }
    public void setAstronautImage(AttachedImage astronautImage) {
        this.astronautImage = astronautImage;
    }

    AttachedImage getArm() {
        return arm;
    }
    public void setArm(AttachedImage arm) {
        this.arm = arm;
    }

    int getHealth() {
        return this.health;
    }
    void setHealth(int health) {
        this.health = health;
    }

    int getArmour() {
        return this.armour;
    }
    void setArmour(int armour) {
        this.armour = armour;
    }

    int getAmmo() {
        return this.ammo;
    }
    void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    float getDamageReduction() {
        return 1 - this.armour / 100f;
    }

    float getDashCooldown() {
        return dashCooldown;
    }
    void setDashCooldown(float dashCooldown) {
        this.dashCooldown = dashCooldown;
    }

    boolean isCanDash() {
        return canDash;
    }
    void setCanDash(boolean canDash) {
        this.canDash = canDash;
    }

    boolean isDPressed() {
        return dPressed;
    }
    void setDPressed(boolean dPressed) {
        this.dPressed = dPressed;
    }

    boolean isAPressed() {
        return aPressed;
    }
    void setAPressed(boolean aPressed) {
        this.aPressed = aPressed;
    }

    int getMovingDir() {
        return movingDir;
    }
    void setMovingDir(int movingDir) {
        this.movingDir = movingDir;
    }

    boolean isBothKeysPressed() {
        return bothKeysPressed;
    }
    void setBothKeysPressed(boolean bothKeysPressed) {
        this.bothKeysPressed = bothKeysPressed;
    }

    boolean isJumping() {
        return jumping;
    }
    void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    int getExtraJumps() {
        return extraJumps;
    }
    void setExtraJumps(int extraJumps) {
        this.extraJumps = extraJumps;
    }

    int getExtraJumpsLimit() {
        return extraJumpsLimit;
    }
    void setExtraJumpsLimit(int extraJumpsLimit) {
        this.extraJumpsLimit = extraJumpsLimit;
    }

    boolean isCanJump() {
        return canJump;
    }
    void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    double getJumpPressedRemember() {
        return jumpPressedRemember;
    }
    void setJumpPressedRemember(double jumpPressedRemember) {
        this.jumpPressedRemember = jumpPressedRemember;
    }

    double getJumpPressedRememberTime() {
        return jumpPressedRememberTime;
    }
    void setJumpPressedRememberTime(double jumpPressedRememberTime) {
        this.jumpPressedRememberTime = jumpPressedRememberTime;
    }
}
