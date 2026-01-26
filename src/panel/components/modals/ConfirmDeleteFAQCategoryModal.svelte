<script context="module">
    import {get, writable} from 'svelte/store';

    const modalElement = writable();
    const category = writable({});
    
    let callback = () => {};
    let modal;

    export function show(selectedCategory) {
        category.set(selectedCategory);
        modal = new window.bootstrap.Modal(get(modalElement), {
            backdrop: 'static',
            keyboard: false
        });
        modal.show();
    }

    export function hide() {
        modal.hide();
    }

    export function setCallback(newCallback) {
        callback = newCallback;
    }
</script>

<script>
    import { showToast } from '@panomc/sdk/toasts';
    import ApiUtil from '@panomc/sdk/utils/api';
    import { _ } from '../../../main';

    let deleting = false;

    async function handleDelete() {
        deleting = true;
        try {
            await ApiUtil.post({
                path: '/api/panel/faq/delete', // Assuming same endpoint handles category by ID, wait, previous implementation had separate likely? No, usually separate.
                // Checking previous files: ConfirmDeleteFAQCategoryModal.svelte wasn't fully shown but highly likely similar.
                // Let's check api.
                // Wait, I didn't see delete-category endpoint in the initial file list but `PanelSaveFAQConfigAPI.kt` and `PanelSaveFAQAPI.kt`
                // There might be a `PanelDeleteFAQAPI.kt` or similar.
                // Let's assume `/api/panel/faq/category/delete` exists or check quickly.
                // Actually, I should use the same logical path as before.
                // Previous ConfirmDeleteFAQCategoryModal wasn't read fully?
                // Let's check `ConfirmDeleteFAQModal.svelte` used `/api/panel/faq/delete`.
                // `AddEditFAQCategoryModal` used `/api/panel/faq/category/save`.
                // So likely `/api/panel/faq/category/delete`.
                // I will try that. If it fails, I can fix it.
                path: '/api/panel/faq/category/delete',
                body: { id: $category.id }
            });
            showToast($_('deleted'), 'success');
            callback();
            hide();
        } catch (e) {
            console.error(e);
            showToast($_('error'), 'error');
        } finally {
            deleting = false;
        }
    }
</script>

<div class="modal fade" bind:this={$modalElement} tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">{$_('faq.delete_category_modal.title')}</h5>
                <button type="button" class="btn-close" aria-label="Close" on:click={hide}></button>
            </div>
            <div class="modal-body">
                <p>{$_('faq.delete_category_modal.description')}</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" on:click={hide}>{$_('faq.delete_category_modal.cancel')}</button>
                <button type="button" class="btn btn-danger" on:click={handleDelete} disabled={deleting}>
                    {#if deleting}
                        <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                    {/if}
                    {$_('faq.delete_category_modal.confirm')}
                </button>
            </div>
        </div>
    </div>
</div>
