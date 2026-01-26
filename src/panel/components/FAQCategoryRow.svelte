<tr>
    <!-- Actions Column -->
    <th scope="row" class="align-middle text-center" style="width: 60px;">
        <div class="dropdown position-static">
            <button type="button" class="btn btn-link link-dark" data-bs-toggle="dropdown" aria-expanded="false" aria-label={$_('actions')}>
                <i class="fas fa-ellipsis-v"></i>
            </button>
            <ul class="dropdown-menu animate__animated animate__fadeIn">
                <li>
                    <button type="button" class="dropdown-item" on:click={() => onEdit(category)}>
                        <i class="fas fa-pen me-2"></i> {$_('faq.edit_category')}
                    </button>
                </li>
                <li>
                    <button type="button" class="dropdown-item text-danger" on:click={() => onDelete(category)}>
                        <i class="fas fa-trash me-2"></i> {$_('faq.delete_category')}
                    </button>
                </li>
            </ul>
        </div>
    </th>

    <!-- ID Column -->
    <td class="align-middle text-center" style="width: 60px;">
        <code>{category.id}</code>
    </td>

    <!-- Name -->
    <td class="align-middle">
        <div class="text-truncate" style="max-width: 300px;">
            <button class="btn btn-link p-0 text-start text-decoration-none fw-bold w-100 text-truncate" 
                    on:click={() => onEdit(category)}>
                {category.name}
            </button>
        </div>
    </td>

    <!-- Display Order -->
    <td class="align-middle text-center">
        {category.displayOrder}
    </td>
</tr>

<script>
    import {_} from '../../main';

    export let category;
    export let onEdit;
    export let onDelete;
</script>
