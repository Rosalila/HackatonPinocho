package rosalila.studio.hackatonpinocho;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.util.Log;

public class GameActivity extends BaseGameActivity {

	private static final String TAG = GameActivity.class.getSimpleName();

	private Camera mCamera;
	
	private ITextureRegion mSplashBackground;

	
	private Scene mSplashScene;
	private Scene mMainMenuScene;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
        mCamera = new Camera(0, 0, GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT);
		EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), mCamera);
		
		return options;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		BuildableBitmapTextureAtlas splashTextureAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), GameConstants.CAMERA_HEIGHT + 1, GameConstants.CAMERA_WIDTH + 1);
		mSplashBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, this, "rosalila_logo.png");
		
		
		try {
			splashTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(1, 1, 1));
		} catch (TextureAtlasBuilderException exception) {
			Log.e(TAG, "Error building splash screen resources");
		}		
		splashTextureAtlas.load();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		mSplashScene = new Scene();
		
		pOnCreateSceneCallback.onCreateSceneFinished(mSplashScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pScene.setBackgroundEnabled(false);
		
		Sprite background = new Sprite(0, 0, mSplashBackground, getVertexBufferObjectManager());
		pScene.attachChild(background);
		
		pScene.registerUpdateHandler(new TimerHandler(GameConstants.SPLASH_DURATION, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mSplashScene.unregisterUpdateHandler(pTimerHandler);
				
				/*Load the main menu scene*/
				
			}
		}));
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
		
		Intent intent = new Intent(this, FightActivity.class);
		startActivity(intent);      
		finish();
	}
	
	public void createMenuResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		BuildableBitmapTextureAtlas mainMenuAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT);
		
		
	}

}
