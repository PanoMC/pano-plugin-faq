<script>
    import {onMount} from 'svelte';
    import ApiUtil from '@panomc/sdk/utils/api';
    import {_} from '../../main';
    import {showToast} from '@panomc/sdk/toasts';

    let config = {
        displayLocation: 'BOTH'
    };
    let loading = true;
    let saving = false;

    onMount(async () => {
        try {
            const res = await ApiUtil.get({ path: '/api/panel/faq/config' });
            config = res.config;
        } catch (e) {
            console.error(e);
        } finally {
            loading = false;
        }
    });

    async function save() {
        saving = true;
        try {
            await ApiUtil.post({ path: '/api/panel/faq/config', body: config });
            showToast($_('faq.settings.saved'), 'success');
        } catch (e) {
            showToast($_('error'), 'error');
        } finally {
            saving = false;
        }
    }
</script>

{#if !loading}
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
                    <option value="BOTH">{$_('faq.settings.location_both')}</option>
                </select>
                <div class="form-text">{$_('faq.settings.display_location_desc')}</div>
            </div>

            <button class="btn btn-primary" on:click={save} disabled={saving}>
                {#if saving}
                    <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                {/if}
                {$_('save')}
            </button>
        </div>
    </div>
{/if}
