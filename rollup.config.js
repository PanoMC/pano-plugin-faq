import { panoPlugin } from '@panomc/plugin-kit/rollup';

// The build is the kit preset (@panomc/plugin-kit): server and client bundles, the entry facade, `panoSdk = 2`,
// the svelte version guard against the SDK pin, PANO_SDK_DIR, DEV and BUNDLE_SDK all live there.
export default panoPlugin();
