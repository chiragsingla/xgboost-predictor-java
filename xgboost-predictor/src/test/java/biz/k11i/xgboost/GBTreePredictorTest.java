package biz.k11i.xgboost;

import biz.k11i.xgboost.learner.ObjFunction;
import biz.k11i.xgboost.util.FVec;
import org.junit.After;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(Theories.class)
public class GBTreePredictorTest extends PredictorTest {

    private static final String MODEL_TYPE = "gbtree";

    @DataPoints
    public static final String[] MODEL_NAMES = {
            "binary-logistic",
            "binary-logitraw",
            "multi-softmax",
            "multi-softprob",
    };

    @DataPoints
    public static final boolean[] USE_JAFAMA = { true, false };

    @Theory
    public void testPredict(String modelName, boolean useJafama) throws IOException {
        ObjFunction.useFastMathExp(useJafama);

        final Predictor predictor = newPredictor("model/" + MODEL_TYPE + "/" + modelName + ".model");

        verifyDouble(MODEL_TYPE, modelName, "predict", new PredictorFunction<double[]>() {
            @Override
            public double[] predict(FVec feat) {
                return predictor.predict(feat);
            }
        });

        verifyDouble(MODEL_TYPE, modelName, "predict_ntree", new PredictorFunction<double[]>() {
            @Override
            public double[] predict(FVec feat) {
                return predictor.predict(feat, false, 1);
            }
        });

        verifyDouble(MODEL_TYPE, modelName, "margin", new PredictorFunction<double[]>() {
            @Override
            public double[] predict(FVec feat) {
                return predictor.predict(feat, true);
            }
        });

        if (modelName.startsWith("binary-")) {
            // test predictSingle()
            verifyDouble(MODEL_TYPE, modelName, "predict", new PredictorFunction<double[]>() {
                @Override
                public double[] predict(FVec feat) {
                    return new double[] {predictor.predictSingle(feat)};
                }
            });
        }

        verifyInt(MODEL_TYPE, modelName, "leaf", new PredictorFunction<int[]>() {
            @Override
            public int[] predict(FVec feat) {
                return predictor.predictLeaf(feat);
            }
        });

        verifyInt(MODEL_TYPE, modelName, "leaf_ntree", new PredictorFunction<int[]>() {
            @Override
            public int[] predict(FVec feat) {
                return predictor.predictLeaf(feat, 2);
            }
        });
    }

    @After
    public void tearDown() {
        ObjFunction.useFastMathExp(false);
    }
}