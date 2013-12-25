package game;

/**
 * @author petter.b.hannevold
 */
public interface LogicInterface {

	public void fold();

	public void bet(double amount);

	public void call();

	public void getGameStatus(Game game);
}
