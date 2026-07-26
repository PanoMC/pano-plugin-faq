import {PanoPlugin} from '@panomc/sdk';
import {derived} from 'svelte/store';
import {_ as i18n} from '@panomc/sdk/utils/language';
import {viewComponent} from '@panomc/sdk/utils/component';
import ApiUtil from '@panomc/sdk/utils/api';
import { showToast } from '@panomc/sdk/toasts';

const pluginId = 'pano-plugin-faq';

// this is to render plugin translations
export const _ = derived(i18n, ($_fn) => {
  return (key, options) => $_fn(`plugins.${pluginId}.${key}`, options);
});

// Success/failure colouring for this plugin's toasts, matching the panel. showToast from
// @panomc/sdk/toasts is the host panel's ToastContainer `show`, whose signature is
// (text, params, toastComponent, options): passing undefined for toastComponent keeps the
// host's DefaultToast, and options.variant maps to Bootstrap's text-success / text-danger.
// These live here rather than in @panomc/sdk/toasts because this plugin is pinned to
// @panomc/sdk 1.0.0-dev.39, which predates the variants; they can be dropped for a direct
// SDK import once that pin moves. On an older panel build the extra argument is ignored and
// the toast renders neutral, so this degrades instead of breaking.
export function showSuccessToast(text, params = {}) {
  return showToast(text, params, undefined, { variant: 'success' });
}

export function showErrorToast(text, params = {}) {
  return showToast(text, params, undefined, { variant: 'danger' });
}

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
      // Theme logic — fetch config via lifecycle for proper SSR context and parallel execution
      pano.ui.app.onLoad(async (data, event) => {
        try {
          const res = await ApiUtil.get({ path: '/api/faq/list', request: event });
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
      });
    }
  }

  onContextUpdate(ctx) { }

  onUnload() { }
}
