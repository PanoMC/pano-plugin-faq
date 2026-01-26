<script>
    import {_} from '../../main';

    export let faq;
    export let onEdit;
    export let onDelete;

    // Helper to strip HTML for preview
    $: answerPreview = faq.answer ? faq.answer.replace(/<[^>]*>?/gm, '') : '';
</script>

<tr>
    <!-- Actions Column (Left, Dropdown) -->
    <th scope="row" class="align-middle text-center" style="width: 60px;">
        <div class="dropdown position-static">
            <button type="button" class="btn btn-link link-dark" data-bs-toggle="dropdown" aria-expanded="false" aria-label={$_('actions')}>
                <i class="fas fa-ellipsis-v"></i>
            </button>
            <ul class="dropdown-menu">
                <li>
                    <button class="dropdown-item" on:click={() => onEdit(faq)}>
                        <i class="fas fa-pen me-2"></i> {$_('faq.edit_faq')}
                    </button>
                </li>
                <li>
                    <button class="dropdown-item text-danger" on:click={() => onDelete(faq)}>
                        <i class="fas fa-trash me-2"></i> {$_('faq.delete_faq')}
                    </button>
                </li>
            </ul>
        </div>
    </th>

    <!-- ID Column (Match Announcement Style) -->
    <td class="align-middle text-center" style="width: 60px;">
        <code>{faq.id}</code>
    </td>

    <!-- Question -->
    <td class="align-middle">
        <button class="btn btn-link p-0 text-start text-decoration-none text-truncate fw-bold link-dark" 
                style="max-width: 300px;" 
                on:click={() => onEdit(faq)}>
            {faq.question}
        </button>
    </td>

    <!-- Category -->
    <td class="align-middle text-nowrap">
        <span class="badge bg-light text-dark border">
            {faq.categoryName}
        </span>
    </td>

    <!-- Active Status -->
    <td class="align-middle text-nowrap">
        {#if faq.isActive}
            <span class="badge text-bg-success">{$_('yes')}</span>
        {:else}
            <span class="badge text-bg-secondary">{$_('no')}</span>
        {/if}
    </td>

    <!-- Display Order -->
    <td class="align-middle text-center">
        {faq.displayOrder}
    </td>
</tr>
