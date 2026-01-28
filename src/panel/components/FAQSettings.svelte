{#if addon?.id === 'pano-plugin-faq'}
  <div class="card">
    <div class="card-header">
      {$_('faq.settings.title')}
    </div>
    <div class="card-body animate__animated animate__fadeIn">
      <!-- Display Location -->
      <div class="row mb-3">
        <label for="displayLocation" class="col-md-6 col-form-label">
          {$_('faq.settings.display_location')}
        </label>
        <div class="col-md-6">
          <select id="displayLocation" class="form-select" bind:value={config.displayLocation}>
            <option value="THEME_PAGE">{$_('faq.settings.location_theme_page')}</option>
            <option value="SUPPORT_PAGE">{$_('faq.settings.location_support_page')}</option>
          </select>
        </div>
      </div>

      <!-- Show Search -->
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="showSearch">
          {$_('faq.settings.show_search')}
        </label>
        <div class="col-md-6 d-flex align-items-center">
          <div class="form-check form-switch">
            <input
              class="form-check-input"
              type="checkbox"
              id="showSearch"
              bind:checked={config.showSearch} />
          </div>
        </div>
      </div>

      <!-- Question Limit -->
      <div class="row mb-3">
        <label for="questionLimit" class="col-md-6 col-form-label">
          <span class="d-block">
            {$_('faq.settings.question_limit')}
          </span>
          <small>{$_('faq.settings.question_limit_desc')}</small>
        </label>
        <div class="col-md-6">
          <input
            type="number"
            class="form-control"
            id="questionLimit"
            bind:value={config.questionLimit}
            min="0" />
        </div>
      </div>

      <div class="mt-4">
        <button class="btn btn-secondary" on:click={save} disabled={saving || !isDirty}>
          {#if saving}
            <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"
            ></span>
          {/if}
          {$_('save')}
        </button>
      </div>
    </div>
  </div>
{/if}

<script>
  import ApiUtil from '@panomc/sdk/utils/api';
  import { _ } from '../../main';
  import { showToast } from '@panomc/sdk/toasts';

  export let addon;

  let config = addon?.config || {
    displayLocation: 'THEME_PAGE',
    showSearch: true,
    questionLimit: 0,
  };
  let initialConfig = JSON.stringify(config);
  let saving = false;

  $: isDirty = JSON.stringify(config) !== initialConfig;

  async function save() {
    saving = true;
    try {
      // Ensure questionLimit is a number
      config.questionLimit = parseInt(config.questionLimit) || 0;
      await ApiUtil.post({ path: '/api/panel/faq/config', body: config });
      if (addon) addon.config = config;
      initialConfig = JSON.stringify(config);
      showToast($_('faq.settings.saved'));
    } catch (e) {
      showToast($_('error'));
    } finally {
      saving = false;
    }
  }
</script>
