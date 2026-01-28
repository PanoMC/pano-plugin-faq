<script>
    import ApiUtil from '@panomc/sdk/utils/api';
    import {_} from '../../main';
    import {showToast} from '@panomc/sdk/toasts';

    export let addon;

    let config = addon?.config || {
        displayLocation: 'THEME_PAGE',
        showSearch: true,
        questionLimit: 0
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

{#if addon?.id === 'pano-plugin-faq'}
    <div class="card">
            <div class="card-header">
                {$_('faq.settings.title')}
            </div>
            <div class="card-body">
                <div class="mb-3">
                    <label for="displayLocation" class="form-label">{$_('faq.settings.display_location')}</label>
                    <select id="displayLocation" class="form-select" bind:value={config.displayLocation}>
                        <option value="THEME_PAGE">{$_('faq.settings.location_theme_page')}</option>
                        <option value="SUPPORT_PAGE">{$_('faq.settings.location_support_page')}</option>
                    </select>
                    <div class="form-text">{$_('faq.settings.display_location_desc')}</div>
                </div>

                <div class="mb-3">
                    <div class="form-check form-switch">
                        <input class="form-check-input" type="checkbox" id="showSearch" bind:checked={config.showSearch}>
                        <label class="form-check-label" for="showSearch">{$_('faq.settings.show_search')}</label>
                    </div>
                    <div class="form-text">{$_('faq.settings.show_search_desc')}</div>
                </div>

                <div class="mb-3">
                    <label for="questionLimit" class="form-label">{$_('faq.settings.question_limit')}</label>
                    <input type="number" class="form-control" id="questionLimit" bind:value={config.questionLimit} min="0">
                    <div class="form-text">{$_('faq.settings.question_limit_desc')}</div>
                </div>

                <button class="btn btn-primary" on:click={save} disabled={saving || !isDirty}>
                    {$_('save')}
                </button>
            </div>
        </div>
{/if}
