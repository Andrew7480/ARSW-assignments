package funcStrategy;
public class Sen implements FuncStrategy {
    @Override
    public double execute(double number) {
        return Math.sin(number);
    }

    @Override
    public String getName() {
        return "Sen";
    }
}
