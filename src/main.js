import {PanoPlugin} from '@panomc/sdk';
import {derived} from 'svelte/store';
import {_ as i18n} from '@panomc/sdk/utils/language';

const pluginId = 'pano-plugin-faq';

// this is to render plugin translations
export const _ = derived(i18n, ($_fn) => {
  return (key, options) => $_fn(`plugins.${pluginId}.${key}`, options);
});

export default class FAQPlugin extends PanoPlugin {
  onLoad() {
    const pano = this.pano;

    console.log('FAQ Plugin enabled, environment:' + pano.isPanel);

    if (pano.isPanel) {
      // here you can write your panel-related codes
    } else {
      // here you can write your theme-related codes
    }
  }

  onContextUpdate(ctx) { }

  onUnload() { }
}
