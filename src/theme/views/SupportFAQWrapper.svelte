{#if !loading}
  <div class="faq-support-faq-wrapper mt-5" style:opacity={searching ? 0.6 : 1} style:transition="opacity 0.2s">
    <h2 class="faq-support-faq-wrapper__title mb-4 text-center">{$_('faq.title')}</h2>
    <FAQList {faqs} {categories} {config} isSearching={searching} onsearch={handleSearch} />
  </div>
{/if}

<script module>
    import { api } from '@panomc/sdk/plugin-api';

    // Registered on the 'theme:support:content' hook by main.js only when the admin picked the support page;
    // a theme can place the block itself with <PluginBlock id="faq:SupportFAQWrapper" />.
    export const view = { block: true };

    export async function load(event) {
    try {
      const res = await api.get({
        path: '/list',
        request: event,
      });
      return res;
    } catch (e) {
      console.error('[FAQ] Hook load failed', e);
      return { faqs: [], categories: [], config: {} };
    }
  }
</script>

<script>
  import { onMount } from 'svelte';
  import { derived } from 'svelte/store';
  import { _ as i18n } from '@panomc/sdk/utils/language';
  import FAQList from './FAQList.svelte';

  // plugin translations: $_('key') reads plugins.pano-plugin-faq.key
  const _ = derived(i18n, ($_fn) => (key, options) => $_fn(`plugins.pano-plugin-faq.${key}`, options));

  let { faqs = [], categories = [], config = {} } = $props();

  let loading = $state(true);
  let searching = $state(false);

  $effect(() => { if (faqs.length > 0) loading = false; });

  async function handleSearch(query) {
    searching = true;
    try {
      const res = await api.get({
        path: '/list' + (query ? `?search=${encodeURIComponent(query)}` : ''),
      });
      faqs = res.faqs;
    } catch (e) {
      console.error(e);
    } finally {
      searching = false;
    }
  }

  onMount(async () => {
    if (faqs.length === 0) {
      try {
        const res = await api.get({ path: '/list' });
        faqs = res.faqs;
        categories = res.categories;
        config = res.config;
      } catch (e) {
        console.error(e);
      } finally {
        loading = false;
      }
    } else {
      loading = false;
    }
  });
</script>
