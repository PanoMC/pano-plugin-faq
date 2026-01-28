import { PanoPlugin } from '@panomc/sdk';
import { derived } from 'svelte/store';
import { _ as i18n } from '@panomc/sdk/utils/language';
import { viewComponent } from '@panomc/sdk/utils/component';
import ApiUtil from '@panomc/sdk/utils/api';

const pluginId = 'pano-plugin-faq';

// this is to render plugin translations
export const _ = derived(i18n, ($_fn) => {
  return (key, options) => $_fn(`plugins.${pluginId}.${key}`, options);
});

export default class FAQPlugin extends PanoPlugin {
  onLoad() {
    const pano = this.pano;

    if (pano.isPanel) {
      pano.ui.addon.onLoad(async (data, event) => {
        if (data.addon.id !== pluginId) return;
        try {
          const res = await ApiUtil.get({
            path: '/api/panel/faq/config',
            request: event,
          });
          data.addon.config = res.config;
        } catch (e) {
          console.error('[FAQ] Failed to load config', e);
        }
      });

      pano.ui.hook.register({
        name: `panel:plugin-detail:content:${pluginId}`,
        component: viewComponent(() => import('./panel/components/FAQSettings.svelte')),
        permission: `pano.plugin.${pluginId}.manage.faq`
      });

      pano.ui.page.register({
        path: '/faq',
        component: viewComponent(() => import('./panel/pages/FAQPage.svelte'))
      });

      pano.ui.page.register({
        path: '/faq/categories',
        component: viewComponent(() => import('./panel/pages/FAQCategoriesPage.svelte'))
      });

      // Panel Navigation
      pano.ui.nav.site.editNavLinks((navItems) => {
        const ticketIndex = navItems.findIndex(n => n.href === '/tickets');
        const newItem = {
          href: '/faq',
          text: `plugins.${pluginId}.faq.title`,
          permission: `pano.plugin.${pluginId}.manage.faq`,
          icon: 'fas fa-question-circle',
          startsWith: false
        };

        if (!navItems.find((n) => n.href === '/faq')) {
          if (ticketIndex !== -1) {
            navItems.splice(ticketIndex + 1, 0, newItem);
          } else {
            navItems.push(newItem);
          }
        }
        return navItems;
      });

    } else {
      // Theme logic

      // 2. Fetch config to know where to show links
      (async () => {
        try {
          const res = await ApiUtil.get({ path: '/api/faq/list' });
          const config = res.config;

          if (config.displayLocation === 'THEME_PAGE') {
            pano.ui.page.register({
              path: '/faq',
              component: viewComponent(() => import('./theme/FAQPage.svelte'))
            });

            pano.ui.nav.site.editNavLinks((navItems) => {
              if (!navItems.find((n) => n.href === '/faq')) {
                navItems.push({
                  href: '/faq',
                  text: `plugins.${pluginId}.faq.title`,
                  target: '_self',
                  startsWith: false,
                });
              }
              return navItems;
            });
          }

          if (config.displayLocation === 'SUPPORT_PAGE') {
            // Inject into Support Page if hook exists
            pano.ui.hook.register({
              name: 'theme:support:content',
              component: viewComponent(() => import('./theme/components/SupportFAQWrapper.svelte'))
            });
          }
        } catch (e) {
          console.error('[FAQ Plugin] Failed to init theme logic', e);
        }
      })();
    }
  }

  onContextUpdate(ctx) { }

  onUnload() { }
}
