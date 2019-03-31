package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * The player character. Holds all the current and past info about the player (if they acquired a certain item, which direction they're moving in, etc.)
 */
public class MainCharacter extends Walker {

    private int health;
    private int armour;
    private int ammo;
    private AttachedImage playerImage;
    private AttachedImage arm;
    private String armImage = "data/Arm.png";
    private boolean acquiredPistol = false;

    private float dashSpeed;
    private float dashCooldown = 0.0f;
    private boolean canDash = false;
    private boolean acquiredBoots = false;

    private boolean dPressed = false;
    private boolean aPressed = false;
    private int movingDir = 0;
    private boolean bothKeysPressed = false;

    private boolean jumping = false;
    private int extraJumps;
    private int extraJumpsLimit;
    private boolean canJump = false;
    private double jumpPressedRemember;

    /**
     * Constructor for MainCharacter.
     * @param world Current world.
     * @param shape The hit box of the player.
     * @param health The health of the player.
     * @param armour The armour of the player.
     * @param ammo The ammo of the player.
     * @param extraJumpsLimit The maximum amount of jumps the player can perform without coming into contact with a solid surface.
     * @param dashSpeed The dash speed of the player.
     * @param level The current level the player is on.
     */
    public MainCharacter(SuperLevel world, Shape shape, int health, int armour, int ammo, int extraJumpsLimit, float dashSpeed, int level) {
        super(world, shape);
        this.health = health;
        this.armour = armour;
        this.ammo = ammo;
        this.extraJumpsLimit = extraJumpsLimit;
        this.extraJumps = this.extraJumpsLimit;
        this.dashSpeed = dashSpeed;
        this.setName("Player");

        this.setGravityScale(9.81f);

        this.playerImage = new AttachedImage(this, new BodyImage("data/Idle.png", 2.2f), 1, 0, new Vec2(0, 0));
        //If the user is in level 6 or is playing a user created level, enable the shooting mechanic.
        if (level > 5 || level == -1) {
            this.armImage = "data/ArmPistol.png";
            this.acquiredPistol = true;
        }
        this.arm = new AttachedImage(this, new BodyImage(armImage, 2), 0.19f, 0, new Vec2(-0.15f, 0));
    }

    /**
     * Called when the player image needs to chane to a different image (such as a jumping image or dashing image).
     * @param path The path to the image file.
     */
    @SuppressWarnings("Duplicates")
    public void changeImages(String path) {
        //If the user is facing left...
        if (this.playerImage.isFlippedHorizontal()) {
            this.removeAllImages();
            this.playerImage = new AttachedImage(this, new BodyImage("data/" + path, 2.2f), 1, 0, new Vec2(0, 0));
            this.arm = new AttachedImage(this, new BodyImage(armImage, 2), 0.19f, 0, new Vec2(-0.15f, 0));

            //This ensures that the player is facing the same direction.
            this.playerImage.flipHorizontal();
            this.arm.flipHorizontal();
        } else if (!this.playerImage.isFlippedHorizontal()) { //If the user is facing right...
            this.removeAllImages();
            this.playerImage = new AttachedImage(this, new BodyImage("data/" + path, 2.2f), 1, 0, new Vec2(0, 0));
            this.arm = new AttachedImage(this, new BodyImage(armImage, 2), 0.19f, 0, new Vec2(-0.15f, 0));
        }
    }

    /**
     * @return the current player image attached to the player.
     */
    public AttachedImage getPlayerImage() {
        return playerImage;
    }

    /**
     * @return the current arm image attached to the player.
     */
    public AttachedImage getArm() {
        return arm;
    }

    /**
     * Set the player's arm to that of a pistol if they collect one.
     * @param armImage The path to the image file.
     */
    public void setArmImage(String armImage) {
        this.armImage = armImage;
    }

    /**
     * Updates the player's current stats (health, armour, ammo) to a set of new ones.
     * @param stats An integer array holding the new player stats.
     */
    public void setStats(int[] stats) {
        this.health = stats[0];
        this.armour = stats[1];
        this.ammo = stats[2];
    }

    /**
     * @return the current health of the player.
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * Set the health of the player to a new one.
     * @param health The new health of the player.
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * @return the current armour of the player.
     */
    public int getArmour() {
        return this.armour;
    }

    /**
     * Set the armour of the player to a new one.
     * @param armour The new armour of the player.
     */
    public void setArmour(int armour) {
        this.armour = armour;
    }

    /**
     * @return the current ammo of the player.
     */
    public int getAmmo() {
        return this.ammo;
    }

    /**
     * Set the ammo of the player to a new one.
     * @param ammo The new ammo of the player.
     */
    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    /**
     * @return the amount of damage reduction based on how much armour the player has.
     */
    public float getDamageReduction() {
        return 1 - this.armour / 100f;
    }

    /**
     * @return the dash cooldown (amount of time to wait before allowing the player to dash again).
     */
    public float getDashCooldown() {
        return dashCooldown;
    }

    /**
     * Set the dashCooldown of the player to a new one.
     * @param dashCooldown The new dashCooldown of the player.
     */
    public void setDashCooldown(float dashCooldown) {
        this.dashCooldown = dashCooldown;
    }

    /**
     * @return canDash (if the player can dash again).
     */
    public boolean isCanDash() {
        return canDash;
    }

    /**
     * Set the canDash of the player to a new one.
     * @param canDash The new canDash of the player.
     */
    public void setCanDash(boolean canDash) {
        this.canDash = canDash;
    }

    /**
     * @return dPressed (if the player is holding d).
     */
    public boolean isDPressed() {
        return dPressed;
    }

    /**
     * Set the dPressed of the player to a new one.
     * @param dPressed The new dPressed of the player.
     */
    public void setDPressed(boolean dPressed) {
        this.dPressed = dPressed;
    }

    /**
     * @return aPressed (if the player is holding a).
     */
    public boolean isAPressed() {
        return aPressed;
    }

    /**
     * Set the aPressed of the player to a new one.
     * @param aPressed The new aPressed of the player.
     */
    public void setAPressed(boolean aPressed) {
        this.aPressed = aPressed;
    }

    /**
     * @return movingDir (the direction the player is moving in as an integer as -1, 0, or 1).
     */
    public int getMovingDir() {
        return movingDir;
    }

    /**
     * Set the movingDir of the player to a new one.
     * @param movingDir The new movingDir of the player.
     */
    public void setMovingDir(int movingDir) {
        this.movingDir = movingDir;
    }

    /**
     * @return bothKeysPressed (if the player is holding down both a and d at the same time).
     */
    public boolean isBothKeysPressed() {
        return bothKeysPressed;
    }

    /**
     * Set the bothKeysPressed of the player to a new one.
     * @param bothKeysPressed The new bothKeysPressed of the player.
     */
    public void setBothKeysPressed(boolean bothKeysPressed) {
        this.bothKeysPressed = bothKeysPressed;
    }

    /**
     * @return jumping (if the player is currently in the air).
     */
    public boolean isJumping() {
        return jumping;
    }

    /**
     * Set the jumping of the player to a new one.
     * @param jumping The new jumping of the player.
     */
    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    /**
     * @return extraJumps (how many jumps the player has left in the air).
     */
    public int getExtraJumps() {
        return extraJumps;
    }

    /**
     * Set the extraJumps of the player to a new one.
     * @param extraJumps The new extraJumps of the player.
     */
    public void setExtraJumps(int extraJumps) {
        this.extraJumps = extraJumps;
    }

    /**
     * @return extraJumpsLimit (the maximum amount of times the player can jump in the air without coming into contact with a solid surface).
     */
    public int getExtraJumpsLimit() {
        return extraJumpsLimit;
    }

    /**
     * Set the extraJumpsLimit of the player to a new one.
     * @param extraJumpsLimit The new extraJumpsLimit of the player.
     */
    public void setExtraJumpsLimit(int extraJumpsLimit) {
        this.extraJumpsLimit = extraJumpsLimit;
    }

    /**
     * @return canJump (if the player is able to jump again - set to true when the player comes into contact with the ground or the jump cooldown has ended).
     */
    public boolean isCanJump() {
        return canJump;
    }

    /**
     * Set the canJump of the player to a new one.
     * @param canJump The new canJump of the player.
     */
    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    /**
     * @return jumpPressedRemember (the amount of time remaining the game will remember if the player pressed jump again after depleting all their available jumps in the air).
     */
    public double getJumpPressedRemember() {
        return jumpPressedRemember;
    }

    /**
     * Set the jumpPressedRemember of the player to a new one.
     * @param jumpPressedRemember The new jumpPressedRemember of the player.
     */
    public void setJumpPressedRemember(double jumpPressedRemember) {
        this.jumpPressedRemember = jumpPressedRemember;
    }

    /**
     * @return jumpPressedRememberTime (the amount of time the game will remember if the player pressed jump again after depleting all their available jumps in the air for).
     */
    public double getJumpPressedRememberTime() {
        return (double) 0.15f;
    }

    /**
     * @return acquiredBoots (if the player has picked up the dash boots).
     */
    public boolean isAcquiredBoots() {
        return acquiredBoots;
    }

    /**
     * Set the acquiredBoots of the player to a new one.
     * @param acquiredBoots The new acquiredBoots of the player.
     */
    public void setAcquiredBoots(boolean acquiredBoots) {
        this.acquiredBoots = acquiredBoots;
    }

    /**
     * @return acquiredPistol (if the player has picked up a pistol).
     */
    public boolean isAcquiredPistol() {
        return acquiredPistol;
    }

    /**
     * Set the acquiredPistol of the player to a new one.
     * @param acquiredPistol The new acquiredPistol of the player.
     */
    public void setAcquiredPistol(boolean acquiredPistol) {
        this.acquiredPistol = acquiredPistol;
    }

    /**
     * @return dashSpeed (how fast the player can dash).
     */
    public float getDashSpeed() {
        return dashSpeed;
    }
}
