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
            showToast($_('faq.toasts.faq_deleted'));
            callback();
            hide();
        } catch (e) {
            console.error(e);
            showToast($_('error'));
        } finally {
            deleting = false;
        }
    }
</script>

<div class="modal fade" bind:this={$modalElement} tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-body text-center">
                <div class="pb-3">
                    <i class="fas fa-question-circle fa-3x d-block m-auto text-muted"></i>
                </div>
                <p>{$_('faq.delete_modal.description')}</p>
            </div>
            <div class="modal-footer flex-nowrap">
                <button type="button" class="btn btn-link col-6 m-0" on:click={hide} disabled={deleting}>{$_('faq.delete_modal.cancel')}</button>
                <button type="button" class="btn btn-danger col-6 m-0" on:click={handleDelete} disabled={deleting}>
                    {#if deleting}
                        <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                    {/if}
                    {$_('faq.delete_modal.confirm')}
                </button>
            </div>
        </div>
    </div>
</div>
