package rosalila.studio.hackatonpinocho;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

public class GameActivity extends BaseGameActivity {

	private Camera mCamera;
	
	private ITextureRegion mSplashBackground;
	
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
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx");
		
		BuildableBitmapTextureAtlas splashTextureAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT);
		mSplashBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, this, "rosalila_logo.png");
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		// TODO Auto-generated method stub

	}

}
