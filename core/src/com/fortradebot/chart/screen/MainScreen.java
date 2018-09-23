package com.fortradebot.chart.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fortradebot.chart.Outrun;
import com.fortradebot.chart.api_for_data.Exmo;
import com.fortradebot.chart.statistic.ZecStats;
import com.fortradebot.chart.strings.ZecData;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Knyazev D.A. for resume on 20.09.2018.
 */

public class MainScreen implements Screen{
    private Outrun outrun;
    private Exmo exmo;
    private UpdateData updateData;
    private Queue<ZecStats> statistic;
    private Viewport viewport;
    private OrthographicCamera camera;
    private float HEIGHT = Gdx.graphics.getHeight();
    private float WIDTH  = Gdx.graphics.getWidth();

    private int SAMPLE_POINTS = 100;
    private float SAMPLE_POINT_DISTANCE = 1f / SAMPLE_POINTS;

    private SpriteBatch spriteBatch;
    private ImmediateModeRenderer20 renderer;
    private Array<Path<Vector2>> paths = new Array<>();
    private int currentPath = 0;
    private final Vector2 tmpV = new Vector2();
    private Vector2 cp[];
    private Queue<Vector2> vectorQueue;

    private float lastPointY; //Coordinates relative to the Y axis. Computed

    private String cash, askTop, currency;

    public MainScreen(Outrun outrun, Exmo exmo){
        this.outrun = outrun;
        this.exmo   = exmo;
        viewport    = new FillViewport(WIDTH, HEIGHT);
        camera      = new OrthographicCamera();
        statistic   = new Queue<>();
    }

    @Override
    public void show() {
        camera.setToOrtho(false, WIDTH, HEIGHT);
        viewport.apply();
        lastPointY = Gdx.graphics.getHeight() / 3;
        updateData = new UpdateData();
        currency   = "USD";
        loadingSpline();
    }

    /**
     * Загрузка сплайна и необходимых элементов для его отображения
     */
    private void loadingSpline(){
        renderer    = new ImmediateModeRenderer20(false, true, 0);
        spriteBatch = new SpriteBatch();
        vectorQueue = new Queue<>();

        for (int i = 0; vectorQueue.size < 80; i++){
            vectorQueue.addFirst(new Vector2(i * 10, lastPointY));
        }

        cp = new Vector2[vectorQueue.size];
        for (int i = 0; i < vectorQueue.size; i++){
            cp[i] = vectorQueue.get(i);
        }

        paths.add(new CatmullRomSpline<>(cp, false));
    }

    private void controlCoinLong() {
        if (statistic.size < 2) {
            statistic.addFirst(new ZecStats());
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ZecData.cash(cash, "ZEC")).append("\n");
        stringBuilder.append(ZecData.cashRub(cash, "USD"));
        stringBuilder.append("\n").append("Top Order Sell ").append(ZecData.askTop(askTop));
        stringBuilder.append("\n").append("Top Order Buy ").append(ZecData.bidTop(askTop));
        stringBuilder.append("\n").append("Orders: \n").append(ZecData.ask(askTop));
        stringBuilder.append("\n").append("Orders: \n").append(ZecData.bid(askTop));

        renderer.begin(spriteBatch.getProjectionMatrix(), GL20.GL_LINE_STRIP);
        float val = 0f;
        while (val <= 1f) {
            paths.get(currentPath).valueAt(/* out: */tmpV, val);
            renderer.vertex(tmpV.x, tmpV.y, 0);
            val += SAMPLE_POINT_DISTANCE;
            //Меняем цвет графика в зависимости от курса
            if (statistic.size > 2 && statistic.first().getAsk() > statistic.get(1).getAsk()){
                renderer.color(Color.GREEN);
            }else if (statistic.size > 2 &&  statistic.first().getAsk() < statistic.get(1).getAsk()){
                renderer.color(Color.RED);
            }
        }
        renderer.end();
    }

    /**
     * Отдельным потоком получаем необходимые данные
     */
    private class UpdateData implements Runnable {
        Thread thread;

        UpdateData() {
            thread = new Thread(this, "Update data flow");
            thread.start();
        }

        /**
         * Производим обновление данных каждые две секунды
         * Каждые ~45 секунд добавляем промежуточные данные для анализа.
         */
        @Override
        public void run() {
            try {
                for (int i = 0; i <= 20; i++) {
                    askTop = exmo.Request("order_book", new HashMap<String, String>() {{
                        put("pair", "ZEC_" + currency);
                        put("limit", "1");
                    }});
                    if (i >= 20) {
                        cash = exmo.Request("user_info", null);
                        statistic.addFirst(new ZecStats());
                        statistic.first().zecStatsParameter(ZecData.getAskDouble(), ZecData.getBidDouble(), currency);
                        controlCoinLong();

                        if (vectorQueue.size >= 80) {
                            vectorQueue.removeFirst();
                            float percent = (((float) statistic.first().getAsk() - (float) statistic.get(1).getAsk()) / (((float) statistic.first().getAsk() + (float) statistic.get(1).getAsk()) / 2)) * 100;
                            lastPointY = lastPointY + (percent * 40); // чем больше процент, тем больше изменение графика при малых суммах
                            for (int y = 0; y < vectorQueue.size; y++){
                                if (vectorQueue.get(y).y <= -1){
                                    vectorQueue.get(y).y = lastPointY;
                                }
                            }
                            System.out.println(statistic.first().getAsk());
                            vectorQueue.addLast(new Vector2(0, lastPointY));

                            for (int x = 0; x < vectorQueue.size; x++) {
                                cp[x] = vectorQueue.get(x);
                                /* В зависимости от ширины экрана и размера vectorQueue задаем параметр первого элемента по оси X
                                   чем шире размер экрана и меньше размер vectorQueue, тем больше шаг между элементами
                                 */
                                cp[x].x = x * (Gdx.graphics.getWidth() / vectorQueue.size);
                            }

                            i = 0;

                            if (statistic.size >= 41) {
                                statistic.removeLast();
                            }
                        }
                    }
                    TimeUnit.SECONDS.sleep(2);
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        renderer.dispose();
    }
}
