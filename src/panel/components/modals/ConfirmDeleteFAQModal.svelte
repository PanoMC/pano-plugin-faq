<script context="module">
    import {get, writable} from 'svelte/store';

    const modalElement = writable();
    const faq = writable({});
    
    let callback = () => {};
    let modal;

    export function show(selectedFaq) {
        faq.set(selectedFaq);
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
                path: '/api/panel/faq/delete',
                body: { id: $faq.id }
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
                <h5 class="modal-title">{$_('faq.delete_modal.title')}</h5>
                <button type="button" class="btn-close" aria-label="Close" on:click={hide}></button>
            </div>
            <div class="modal-body">
                <p>{$_('faq.delete_modal.description')}</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" on:click={hide}>{$_('faq.delete_modal.cancel')}</button>
                <button type="button" class="btn btn-danger" on:click={handleDelete} disabled={deleting}>
                    {#if deleting}
                        <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                    {/if}
                    {$_('faq.delete_modal.confirm')}
                </button>
            </div>
        </div>
    </div>
</div>
