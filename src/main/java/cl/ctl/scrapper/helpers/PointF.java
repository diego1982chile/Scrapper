package cl.ctl.scrapper.helpers;

/**
 * Created by des01c7 on 15-12-20.
 */
class PointF
{

    public float x, y;

    public PointF(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString()
    {
        return "(" + x + "," + y + ")";
    }
}
